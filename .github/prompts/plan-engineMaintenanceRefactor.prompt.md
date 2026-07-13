## Plan: Refaktor silnika pod maintenance

Celem jest podniesienie utrzymywalności i przewidywalności silnika bez przepisywania całej architektury. Podejście: najpierw wzmocnienie kontraktów domenowych i granic warstw, potem iteracyjne domykanie mechanik taktycznych wymaganych przez plan produktu, z pełnym wsparciem testowym i aktualizacją traceability.

**Steps**
1. Faza 1 [ZREALIZOWANE]: Ustalenie kontraktów i inwariantów domeny. Zdefiniować docelowe inwarianty dla komend ruchu i ich wyników (Accepted, UnknownUnit, InvalidTarget, ReplacedExisting) oraz zatwierdzić zakres refaktoru API runtime. Ta faza jest bazą dla wszystkich kolejnych kroków.
2. Faza 1 [ZREALIZOWANE]: Wprowadzić silniejsze typy wartości w domenie (identyfikatory i współrzędne) oraz ograniczyć primitive obsession w newralgicznych ścieżkach. Krok zależy od kroku 1.
3. Faza 2 [ZREALIZOWANE]: Rozdzielić odpowiedzialności komend i projekcji ruchu: zapis rozkazów do stanu oddzielić od projekcji/interpolacji wykorzystywanej przez render. Krok zależy od kroku 1 i 2.
4. Faza 2: Uporządkować przechowywanie rozkazów do postaci OrderBook z jawną regułą pojedynczego aktywnego MOVE na jednostkę i deterministyczną polityką nadpisania. Krok zależy od kroku 3.
5. Faza 2: Ujednolicić semantykę UI state machine dla selekcji i trybu ruchu tak, aby nazwy operacji odpowiadały faktycznym przejściom stanów. Krok może iść równolegle z krokiem 4 po zakończeniu kroku 3.
6. Faza 3: Wyodrębnić resolvery faz (Movement, Combat, Retreat) z jednolitym kontraktem wejście/wyjście i zachować pełną deterministyczność orkiestracji tury. Krok zależy od kroku 4.
7. Faza 3: Zaimplementować ruch kosztowy z deterministycznym tie-break (REQ-MOVE-002) jako pierwszy pełny mechanizm gameplay ponad baseline. Krok zależy od kroku 6.
8. Faza 3: Dodać politykę stackingu i ograniczeń współlokacji (REQ-STACK-001) wraz z testami kolizji wielojednostkowych. Krok zależy od kroku 7.
9. Faza 3: Domknąć minimalnie użyteczny resolver walki kontekstowej (REQ-CBT-001) oparty o pozycję, teren i stan jednostki, bez rozszerzeń poza wymagany baseline. Krok zależy od kroku 8.
10. Faza 4: Uzupełnić testy regresji, testy własności deterministycznych, ArchUnit oraz dokumentację traceability po każdym ukończonym mechanizmie. Krok zależy od kroku 9.

**Relevant files**
- /workspaces/tactics-and-strategy/core/src/main/java/game/domain/Unit.java — model jednostki i punkt wejścia do wzmocnienia typów domenowych.
- /workspaces/tactics-and-strategy/core/src/main/java/game/domain/Order.java — kontrakt rozkazu i typowanie danych wejściowych do resolverów.
- /workspaces/tactics-and-strategy/core/src/main/java/game/domain/CampaignState.java — centralny nośnik stanu, inwarianty kolekcji i spójność mutacji przez kopie.
- /workspaces/tactics-and-strategy/core/src/main/java/game/engine/GameRuntime.java — obecne miejsce sprzężenia komend ruchu i projekcji, główny cel rozdziału odpowiedzialności.
- /workspaces/tactics-and-strategy/core/src/main/java/game/engine/TurnEngine.java — orkiestracja faz i kontrakt deterministyczny.
- /workspaces/tactics-and-strategy/core/src/main/java/game/engine/SimultaneousMovePhaseExecutor.java — aktualna logika ruchu oparta o pendingOrders.
- /workspaces/tactics-and-strategy/core/src/main/java/game/engine/CombatPhaseExecutor.java — placeholder do zastąpienia resolverem walki.
- /workspaces/tactics-and-strategy/core/src/main/java/game/engine/RetreatPhaseExecutor.java — placeholder do zastąpienia resolverem odwrotu.
- /workspaces/tactics-and-strategy/core/src/main/java/game/engine/RtsMovementTracker.java — projekcja ruchu dla warstwy renderowania.
- /workspaces/tactics-and-strategy/core/src/main/java/game/screens/SelectionState.java — semantyka przejść wyboru i trybu wskazania celu.
- /workspaces/tactics-and-strategy/core/src/main/java/game/screens/MapPanel.java — integracja UI z komendami ruchu i flow wydawania rozkazów.
- /workspaces/tactics-and-strategy/core/src/test/java/game/OneTurnSimulationTest.java — punkt kontroli równoważności stepwise i monolithic.
- /workspaces/tactics-and-strategy/core/src/test/java/game/GameRuntimeMoveTargetPersistenceTest.java — kontrakty zapisu i nadpisania MOVE.
- /workspaces/tactics-and-strategy/core/src/test/java/game/ArchitecturePackageInfoTest.java — reguły granic warstw i nullability.
- /workspaces/tactics-and-strategy/docs/engine/turn-semantics.md — źródło kontraktu semantyki tur.
- /workspaces/tactics-and-strategy/docs/engine/traceability-matrix.md — obowiązkowa aktualizacja mapowania REQ po zmianach.
- /workspaces/tactics-and-strategy/docs/requirements/game-requirements-plan.md — źródło zakresu REQ-MOVE-002, REQ-STACK-001, REQ-CBT-001.

**Verification**
1. Uruchomić pełny zestaw testów: ./gradlew test.
2. Uruchomić build z kontrolą statyczną: ./gradlew build.
3. Potwierdzić deterministyczność: OneTurnSimulationTest.oneTurn_stepwiseSessionMatchesMonolithicRun oraz porównania semantyczne po refaktorze.
4. Potwierdzić kontrakty komend ruchu i nadpisywania MOVE: GameRuntimeMoveTargetPersistenceTest.
5. Potwierdzić granice architektury i nullability: ArchitecturePackageInfoTest oraz task nullabilityCheck.
6. Wykonać smoke run środowiska headless: ./gradlew headless:run.
7. Zaktualizować traceability i zweryfikować zgodność REQ w docs/engine/traceability-matrix.md.

**Decisions**
- W scope: poprawa kontraktów domenowych, rozdzielenie odpowiedzialności runtime, wdrożenie mechanik ruchu kosztowego, stackingu i bazowego combat resolvera zgodnie z planem wymagań.
- Poza scope: pełna migracja do ECS, multiplayer, rozbudowane AI operacyjne i redesign UI niezwiązany z flow rozkazów.
- Strategia dostarczenia: iteracyjnie, po jednej mechanice na zmianę, z zielonymi testami i aktualizacją traceability na końcu każdej iteracji.

**Further Considerations**
1. Model OrderBook: opcja A jako struktura mapująca UnitId do aktywnego rozkazu, opcja B jako event log z materializacją widoku; rekomendacja: opcja A na obecnym etapie dla mniejszego kosztu utrzymania.
2. Zakres pierwszego combat resolvera: opcja A minimalny deterministic baseline zgodny z REQ, opcja B szerszy model z morale i supply; rekomendacja: opcja A, a rozszerzenia dopiero po stabilizacji.
3. Wdrożenie typów wartości: opcja A big-bang, opcja B inkrementalnie od ścieżek ruchu; rekomendacja: opcja B dla niższego ryzyka regresji.