---
description: "Prowadzi rozmowe zbierajaca wymagania do nowej funkcjonalnosci: problem, zakres, kryteria akceptacji, ryzyka, pytania otwarte i propozycje aktualizacji dokumentacji."
name: "P New Feature Requirements Discovery"
argument-hint: "Opisz pomysl/funkcjonalnosc, kontekst biznesowy i to, co juz jest znane."
agent: "agent"
model: "GPT-5 (copilot)"
---
Pomoz mi przeprowadzic uporzadkowana dyskusje analityczna dla nowej funkcjonalnosci w tym projekcie.

Kontekst dokumentacyjny (uzyj jako punktu odniesienia):
- [Wymagania funkcjonalne - baseline](../../docs/requirements/game-requirements-functional.md)
- [Wymagania niefunkcjonalne - baseline](../../docs/requirements/game-requirements-non-functional.md)
- [Plan wymaganiowy](../../docs/requirements/game-requirements-plan.md)
- [Macierz sledzenia](../../docs/engine/traceability-matrix.md)

Cel:
- Dopytaj mnie o opis problemu, wartosc biznesowa, zakres, ograniczenia i zaleznosci.
- Wypracuj kryteria akceptacji i ryzyka.
- Zadawaj pytania otwarte i wracaj aktywnie z kolejnymi pytaniami doprecyzowujacymi, gdy odpowiedzi sa niejednoznaczne.
- Zaproponuj, jak nowa funkcjonalnosc powinna zostac dopisana do planu i dokumentacji wymagan.

Sposob pracy:
1. Najpierw podsumuj w 3-6 punktach, co zrozumiales z mojego opisu.
2. Zadaj pierwsza serie pytan otwartych (5-10), pogrupowanych tematycznie:
   - problem i cel
   - zakres i granice
   - aktorzy/scenariusze uzycia
   - kryteria akceptacji
   - ryzyka i niewiadome
3. Po kazdej mojej odpowiedzi:
   - zaktualizuj "roboczy obraz wymagania",
   - wskaz, co jest juz jasne, a co nadal nie,
   - zadaj kolejna, krotsza serie pytan doprecyzowujacych.
4. Pilnuj rozroznienia:
   - wymagania funkcjonalne,
   - wymagania niefunkcjonalne,
   - elementy do dopisania do planu implementacyjnego.
5. Nie zakladaj brakujacych danych. Gdy sa luki, wyraznie je oznacz i pytaj dalej.

Na koncu kazdej iteracji zwracaj wynik w formacie:

## Co juz wiemy
- ...

## Otwarte pytania
- ...

## Wstepne wymagania funkcjonalne
- REQ-NEW-FUNC-001: ...

## Wstepne wymagania niefunkcjonalne
- REQ-NEW-NFR-001: ...

## Wstepne kryteria akceptacji
- AC-001: ...

## Ryzyka i zalozenia
- RISK-001: ...

## Proponowane aktualizacje dokumentacji
- `docs/requirements/game-requirements-plan.md`: co dopisac/zmienic
- `docs/requirements/game-requirements-functional.md`: co dopisac/zmienic (jesli to juz baseline implementacji)
- `docs/requirements/game-requirements-non-functional.md`: co dopisac/zmienic (jesli dotyczy)
- `docs/engine/traceability-matrix.md`: jakie identyfikatory wymagania beda potrzebne po implementacji

## Bloki gotowe do wklejenia
- `docs/requirements/game-requirements-plan.md`:
   - Zwroc gotowy blok Markdown do wklejenia w odpowiedniej sekcji planu.
- `docs/requirements/game-requirements-functional.md`:
   - Zwroc gotowy blok Markdown z nowymi wymaganiami funkcjonalnymi (jesli wymaganie przechodzi do baseline implementacji).
- `docs/requirements/game-requirements-non-functional.md`:
   - Zwroc gotowy blok Markdown z nowymi wymaganiami niefunkcjonalnymi (jesli dotyczy).
- `docs/engine/traceability-matrix.md`:
   - Zwroc gotowy blok Markdown z proponowanym mapowaniem REQ -> implementacja -> testy.

W kazdym bloku stosuj identyfikatory zaczynajace sie od `REQ-` i zachowuj spojnosc nazewnictwa z istniejaca dokumentacja.

Regula numeracji REQ:
- Przed zaproponowaniem nowych identyfikatorow sprawdz istniejace wpisy w dokumentach i zaproponuj kolejne wolne identyfikatory zamiast stalych placeholderow.
- Jesli nie da sie jednoznacznie wyznaczyc numeru, zaproponuj 2-3 bezpieczne warianty i oznacz rekomendowany.

Jakosc odpowiedzi:
- Priorytetyzuj pytania, ktore zmniejszaja ryzyko blednej implementacji.
- Unikaj pytan zamknietych, gdy temat wymaga eksploracji.
- Wskazuj potencjalne konflikty zakresu i ukryte koszty.
- Pisz konkretnie i krotko, ale nie pomijaj kluczowych niepewnosci.