---
description: "Implement requirement from REQ ID, update functional/non-functional docs, traceability matrix, and physically remove completed item from plan."
name: "Implement Requirement From Plan"
argument-hint: "Podaj identyfikator REQ z planu (oraz opcjonalnie scope/uwagi)."
agent: "agent"
model: "GPT-5 (copilot)"
---
Zaimplementuj wymaganie na podstawie identyfikatora REQ z planu podanego przez uzytkownika.

Kontekst dokumentacyjny i zrodlowy:
- [Plan wymaganiowy](../../docs/requirements/game-requirements-plan.md)
- [Wymagania funkcjonalne](../../docs/requirements/game-requirements-functional.md)
- [Wymagania niefunkcjonalne](../../docs/requirements/game-requirements-non-functional.md)
- [Macierz sledzenia](../../docs/engine/traceability-matrix.md)
- [Instrukcja traceability dla Java](../instructions/java-traceability.instructions.md)

Zasady pracy:
1. Odczytaj pozycje z `game-requirements-plan.md` wskazana przez REQ z argumentu i wyodrebnij:
   - identyfikator(y) REQ,
   - oczekiwane zachowanie,
   - ograniczenia funkcjonalne i niefunkcjonalne,
   - potencjalne miejsca implementacji i testow.
2. Jesli REQ jest niejednoznaczny albo nie istnieje, zatrzymaj sie i zadaj minimalny zestaw pytan doprecyzowujacych.
3. Upewnij sie, ze istniejace pozycje planu maja przypisane REQ. Jesli w planie sa pozycje bez REQ, uzupelnij je o kolejne wolne identyfikatory w spojnym schemacie numeracji.
4. Wprowadz implementacje w kodzie.
5. Dodaj lub zaktualizuj testy pokrywajace zmiane (unit/integration, zgodnie z kontekstem).
6. Zaktualizuj dokumentacje:
   - `docs/requirements/game-requirements-functional.md` (co zostalo zaimplementowane funkcjonalnie),
   - `docs/requirements/game-requirements-non-functional.md` (wplyw niefunkcjonalny),
   - `docs/engine/traceability-matrix.md` (REQ -> implementacja -> testy),
   - `docs/requirements/game-requirements-plan.md` (fizycznie usun zrealizowana pozycje z planu To-Do).
7. Zachowaj spojnosc identyfikatorow REQ i nie tworz nowych ID bez uzasadnienia.
8. Uruchom adekwatne testy i zglos wynik.

Wymagany format odpowiedzi:
## Zrozumienie wymagania
- REQ z planu: ...
- Powiazane REQ: ...
- Zakres implementacji: ...

## Zmiany w kodzie
- [sciezka](sciezka): krotki opis zmiany

## Zmiany w testach
- [sciezka](sciezka): jakie scenariusze pokryto

## Zmiany w dokumentacji
- [docs/requirements/game-requirements-functional.md](../../docs/requirements/game-requirements-functional.md): ...
- [docs/requirements/game-requirements-non-functional.md](../../docs/requirements/game-requirements-non-functional.md): ...
- [docs/engine/traceability-matrix.md](../../docs/engine/traceability-matrix.md): ...
- [docs/requirements/game-requirements-plan.md](../../docs/requirements/game-requirements-plan.md): ...

## Wynik walidacji
- Uruchomione testy: ...
- Status: ...
- Ryzyka / luki: ...

Jakosc i ograniczenia:
- Nie zakladaj brakujacych danych; gdy brakuje kontekstu, pytaj.
- Plan zawiera tylko pozycje niewykonane, dlatego po wdrozeniu wymagania usun pozycje fizycznie z planu.
- Priorytet: poprawna implementacja + traceability + test evidence.
