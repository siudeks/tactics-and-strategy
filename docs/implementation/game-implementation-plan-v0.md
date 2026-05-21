# Plan implementacji gry v0 (Desert Rats)

## Cel etapu
- Dostarczyć **działającą grę dla 2 osób** z możliwością **przesuwania jednostek** na mapie.
- Plan bazuje na instrukcji `assets/original/desert-rats-zx/DesertRats.pdf`.
- Jeśli instrukcja rozróżnia warianty sprzętowe, przyjmujemy **ZX Spectrum 128K** (a gdy brak danych 128K, zachowujemy zgodność z bazowym Spectrum).

## Założenia źródłowe (z instrukcji)
- Gra obsługuje 1 lub 2 graczy; dla nas priorytetem jest 2 graczy.
- Sekwencja tury: wydanie rozkazów przez obie strony, jednoczesny ruch, potem rozstrzygnięcie kontaktu bojowego.
- Rozkazy jednostek obejmują m.in. `MOVE`, `ASSAULT`, `HOLD`, `TRAVEL`, `FORTIFY`.
- W wariancie 128K: 8 scenariuszy, limit stackingu 10, zwiększony zasięg zaopatrzenia.

## Zakres v0 (MVP do kolejnych kroków)
1. **Tryb hot-seat dla 2 graczy**
   - Strona A (Allies) i Strona B (Axis).
   - Naprzemienne wydawanie rozkazów jednostkom każdej strony.
2. **Ruch jednostek po mapie**
   - Wybór jednostki.
   - Wskazanie celu ruchu.
   - Przesunięcie zgodnie z limitem ruchu jednostki (MPS) i kosztem terenu.
3. **Minimalna walidacja zasad ruchu**
   - Brak wejścia na pola nieprzechodnie.
   - Brak ruchu poza mapę.
   - Respektowanie limitu stackingu (docelowo 10 dla wariantu 128K).
4. **Czytelny stan tury w UI**
   - Widoczna aktywna strona, numer tury i status wybranej jednostki.

## Kolejność realizacji
1. **Model domeny**
   - Typy: `Side`, `Unit`, `Order`, `TurnState`, `MapTile`, `TerrainType`.
   - Dane jednostki: pozycja, punkty ruchu, strona, aktualny rozkaz.
2. **System tur**
   - Faza wydawania rozkazów: najpierw Allies, potem Axis.
   - Faza wykonania: ruch jednostek (na początek sekwencyjnie technicznie, z interfejsem przygotowanym pod jednoczesność).
3. **System ruchu**
   - Pathfinding zgodny z kosztem terenu.
   - Egzekwowanie limitu ruchu i zasad przechodniości.
4. **Interfejs gry**
   - Wybór jednostki i celu na mapie.
   - Panel rozkazów minimum: `MOVE`, `HOLD`.
   - Komunikaty błędów walidacji ruchu (np. niedostępny cel).
5. **Scenariusz startowy**
   - Jeden prosty scenariusz testowy dla 2 graczy (mała mapa, kilka jednostek obu stron).
6. **Testy i walidacja**
   - Testy logiki ruchu (koszt, zasięg, blokady).
   - Testy systemu tur (kolejność stron, reset punktów ruchu).
   - Smoke test uruchomienia gry i wykonania pełnej tury.

## Kryteria akceptacji v0
- Dwóch graczy może rozpocząć partię i naprzemiennie wydawać rozkazy swoim jednostkom.
- Jednostka może zostać przesunięta na legalne pole zgodnie z punktami ruchu.
- Niedozwolony ruch jest blokowany i sygnalizowany użytkownikowi.
- Po zakończeniu obu faz rozkazów tura przechodzi do kolejnej.

## Poza zakresem tego etapu
- Pełny model walki, morale i zaopatrzenia.
- Pełna kampania i wszystkie scenariusze historyczne.
- AI przeciwnika.

## Powiązanie z dokumentacją repozytorium
- Ten plan uszczegóławia `docs/requirements/game-requirements-v0.md` i stanowi kolejny krok realizacyjny.
