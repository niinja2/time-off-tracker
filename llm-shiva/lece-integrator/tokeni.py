"""Zbroji potrošnju tokena iz transkripata subagenata (.jsonl).

Upotreba: python tokeni.py [--model=haiku|sonnet] <naziv>=<putanja.jsonl> [...]
Cijene u $ po milijunu tokena. Za Haiku prag 100K gleda ukupni ulaz jednog poziva
(ulaz + čitanje cachea + upis u cache). Cache multiplikatori su standardni (pretpostavka):
čitanje 0.1x ulaza, upis 1.25x ulaza.
"""
import json
import sys

PRAG = 100_000
MODELI = {  # (ulaz, izlaz) u $/MTok, do praga i iznad praga
    "haiku": {"do_praga": (0.10, 0.50), "iznad_praga": (0.50, 2.50)},
    "sonnet": {"do_praga": (2.00, 10.00), "iznad_praga": (2.00, 10.00)},
}
CIJENE = MODELI["haiku"]
CACHE_CITANJE = 0.1
CACHE_UPIS = 1.25


def zbroji(putanja):
    z = dict(pozivi=0, ulaz=0, izlaz=0, cache_citanje=0, cache_upis=0,
             max_kontekst=0, iznad_praga=0, dolari=0.0)
    vidjeni = set()
    for redak in open(putanja, encoding="utf-8"):
        try:
            d = json.loads(redak)
        except json.JSONDecodeError:
            continue
        poruka = d.get("message") or {}
        u = poruka.get("usage")
        if d.get("type") != "assistant" or not u:
            continue
        # jedan odgovor modela može biti zapisan u više redaka (po bloku sadržaja)
        kljuc = poruka.get("id") or d.get("uuid")
        if kljuc in vidjeni:
            continue
        vidjeni.add(kljuc)
        ulaz = u.get("input_tokens", 0)
        citanje = u.get("cache_read_input_tokens", 0)
        upis = u.get("cache_creation_input_tokens", 0)
        izlaz = u.get("output_tokens", 0)
        kontekst = ulaz + citanje + upis
        razina = "iznad_praga" if kontekst > PRAG else "do_praga"
        c_ulaz, c_izlaz = CIJENE[razina]
        z["pozivi"] += 1
        z["ulaz"] += ulaz
        z["izlaz"] += izlaz
        z["cache_citanje"] += citanje
        z["cache_upis"] += upis
        z["max_kontekst"] = max(z["max_kontekst"], kontekst)
        z["iznad_praga"] += razina == "iznad_praga"
        z["dolari"] += (ulaz * c_ulaz + citanje * c_ulaz * CACHE_CITANJE
                        + upis * c_ulaz * CACHE_UPIS + izlaz * c_izlaz) / 1e6
    return z


def main():
    global CIJENE
    argumenti = sys.argv[1:]
    if argumenti and argumenti[0].startswith("--model="):
        CIJENE = MODELI[argumenti.pop(0).split("=", 1)[1]]
    stupci = ["pozivi", "ulaz", "izlaz", "cache_citanje", "cache_upis",
              "max_kontekst", "iznad_praga", "dolari"]
    print("| sesija | " + " | ".join(stupci) + " |")
    print("| --- " * (len(stupci) + 1) + "|")
    ukupno = dict.fromkeys(stupci, 0)
    for arg in argumenti:
        naziv, putanja = arg.split("=", 1)
        z = zbroji(putanja)
        for s in stupci:
            ukupno[s] = max(ukupno[s], z[s]) if s == "max_kontekst" else ukupno[s] + z[s]
        print(f"| {naziv} | " + " | ".join(
            f"{z[s]:.3f}" if s == "dolari" else str(z[s]) for s in stupci) + " |")
    print("| ukupno | " + " | ".join(
        f"{ukupno[s]:.3f}" if s == "dolari" else str(ukupno[s]) for s in stupci) + " |")


if __name__ == "__main__":
    main()
