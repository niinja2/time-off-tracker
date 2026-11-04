# Metoda reviewa

Općeniti pristup za review koda, specifikacije, testova ili dokumenata. Izveden iz izvođenja od 30. 10. do 1. 11. 2026. ([usporedba.md](usporedba.md), [zakljucci.md](zakljucci.md)). Svaki korak je zasebna, svježa sesija.

Glavni ulaz za sve vrste reviewa je odjeljak "Reviewi: sve na jednom mjestu" u [docs/za-procjenu.md](../docs/za-procjenu.md); ovaj fajl je lokalna kopija s detaljima.

## Koraci

1. **Haiku: inventar.** Neutralni popis onoga što se reviewa, bez ocjena: za kod fajlovi, za specifikaciju pravila i Q-ovi, za testove klase i testovi, za dokumente odjeljci. Ostali koraci ga koriste za orijentaciju ([primjer](lece-integrator/mapa.md)).
2. **Haiku: širina.** n sesija, svaka s dvije srodne uske leće. Leća traži jednu vrstu problema. Leće ovise o tome što se traži; predlažu ih vlasnik ili LLM, a vlasnik odobrava. Parovi se slažu po tome što leće čitaju.
3. **Sonnet medium: dubina.** Jedan širok prompt, za ono između leća i ono što traži pravila i dokumente.
4. **Sonnet medium: sudac.** Nova sesija, samo čita. Dobije sve izlaze 2 i 3 i popis poznatih nalaza, ako postoji. Za svaki nalaz: je li stvaran (otvori `fajl:redak`), je li već poznat, ponavlja li se. Izlaz je jedan popis; novi nalazi dobivaju sljedeće oznake i dopunjuju [nalazi.md](nalazi.md).

**Po potrebi Opus** presudi sporne nalaze ili usporedi izvođenja.

## Pravila

- Svaki nalaz ima `fajl:redak` i doslovan citat; inače NEPROVJERLJIV.
- U prompt ide popis svjesno izostavljenog; takve nalaze reviewer navodi kao PRIHVAĆENO.
- Nova vrsta propusta znači novu leću, ne jači model.
- Što može stroj (duplikati, mrtav kod, slojevi, oznake u testovima), rade alati, ne LLM.
- Trošak se mjeri skriptom iz transkripta ([tokeni.py](lece-integrator/tokeni.py)). Haiku poskupi 5× iznad 100K konteksta po pozivu.

## Na čemu se temelji (integrator, kod)

| Korak | Izvođenje | Rezultat | Trošak |
| --- | --- | --- | --- |
| 2 | Haiku, 5 sesija po 2 leće | 39 od 56, ključni 8 od 9 | $0,47 |
| 2 (alternativa) | Haiku, 10 zasebnih sesija | 32–33 od 56, ključni 6–7 od 9 | $0,48 |
| 3 | Sonnet medium, širok prompt | 28 od 56, ključni 9 od 9 | ~$0,83 |
| 4 | Sonnet medium kao sudac | slaže se s Opus medium sucem (32 prema 33 od 56) | $0,74 |

Zasebne leće nisu bolje od parova, a koštaju isto.

## Otvoreno

- Svaka varijanta izvedena je jednom; razlike od nekoliko nalaza mogu biti šum.
- Shiva (dokumenti prema odgovorima naručitelja) je druga vrsta reviewa; tamo je Opus high bio najjači, pa je za nju ova metoda hipoteza.
- Koliko metoda propušta, znat će se tek s ubačenim bugovima.
- Može li Haiku biti sudac (korak 4) za samu provjeru dokaza: izmjeriti na 99 provjerenih nalaza.
