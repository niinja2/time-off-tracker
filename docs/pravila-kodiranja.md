# Pravila kodiranja

Upute za LLM koji piše kod. Specifikacija je [specifikacija.md](zajednicko/specifikacija.md); ona je jedini sudac ponašanja.

## Načela

1. **Vrata na ulazu i izlazu.** Svaka cjelina provjeri ulaz na početku i jamči izlaz na kraju. Unutarnji koraci ne ponavljaju provjere.
2. **Validacija na granici.** Podaci se provjeravaju kad ulaze u sustav (controller, servis). Unutra se smatraju ispravnima.
3. **Pregrade.** Greška u jednoj cjelini ne ruši druge.
4. **Jedna odgovornost.** Svaka klasa radi jednu stvar.
5. **Jedno mjesto za svako pravilo.** Proširenja se dodaju na jedno mjesto, npr. blagdani u funkciju za radne dane.
6. **Fail fast.** Greška se javlja odmah, s jasnom porukom i statusom iz specifikacije (odjeljak 8).
7. **Enkapsulacija.** Zahtjev sam zna koji su mu prijelazi stanja dopušteni.
8. **Tablice umjesto if-else.** Prijelazi stanja, ovlasti i vidljivost su tablice u kodu (obična mapa ili enum, bez biblioteka za pravila). Testovi prolaze kroz svaki red tablice. Validacija (preklapanje, saldo, datumi) ostaje u malim funkcijama.
9. **Bez overengineeringa.** Ovo je POC. Ne uvodimo uzorke ni biblioteke koje specifikacija ne traži.

## Stil

Kod se piše tako da ga je lako debugirati.

1. **Najviše jedna razina gniježđenja.** Ako bi trebalo dvije, razbij kod u međukorake ili rane izlaze.
2. **Ugniježđeni pozivi razbijaju se u varijable s imenom.**
3. **Rani izlaz umjesto ugniježđenih if-ova.** Provjere na početku funkcije, posao na kraju.
4. **Više od tri argumenta: svaki u svoj redak.**
5. **Lanac poziva: svaki korak u svoj redak.**

### Primjeri

**Ugniježđeni pozivi.** Loše:

```java
if (balanceService.remaining(userRepository.findById(request.userId()).orElseThrow(), request.year()) < workingDays.count(request.start(), request.end())) {
```

Dobro:

```java
User user = userRepository.findById(request.userId()).orElseThrow();
int remaining = balanceService.remaining(user, request.year());
int requested = workingDays.count(request.start(), request.end());

if (requested > remaining) {
```

**Ugniježđeni if-ovi.** Loše:

```java
if (request.isPending()) {
    if (!request.isOwnedBy(actor)) {
        if (canApprove(actor, request)) {
            request.approve(actor);
        }
    }
}
```

Dobro:

```java
if (!request.isPending()) {
    throw new ConflictException("Only pending requests can be approved");
}
if (request.isOwnedBy(actor) && !actor.isAdmin()) {
    throw new ForbiddenException("You cannot approve your own request");
}
if (!canApprove(actor, request)) {
    throw new ForbiddenException("You cannot approve this request");
}

request.approve(actor);
```

**Više od tri argumenta:**

```java
TimeOffRequest request = new TimeOffRequest(
        user,
        type,
        startDate,
        endDate,
        reason
);
```

**Lanac poziva.** Loše:

```java
return requests.stream().filter(r -> r.isApproved() || r.isPending()).filter(r -> r.overlaps(start, end)).findAny().isPresent();
```

Dobro:

```java
return requests.stream()
        .filter(r -> r.isApproved() || r.isPending())
        .filter(r -> r.overlaps(start, end))
        .findAny()
        .isPresent();
```
