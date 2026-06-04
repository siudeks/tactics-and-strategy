package game;

import com.tngtech.archunit.core.domain.JavaClass;
import org.jspecify.annotations.NullMarked;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.core.importer.ImportOption;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "game", importOptions = {
    ImportOption.DoNotIncludeTests.class,
    ImportOption.DoNotIncludeJars.class
})
class ArchitecturePackageInfoTest {

    @ArchTest
    static final ArchRule allApplicationPackagesMustDefinePackageInfo = classes()
        .that().resideInAPackage("game..")
        .and().doNotHaveSimpleName("package-info")
        .should(resideInPackageWithPackageInfo());

    @ArchTest
    static final ArchRule allApplicationPackagesMustBeNullMarked = classes()
        .that().haveSimpleName("package-info")
        .and().resideInAPackage("game..")
        .should().beAnnotatedWith(NullMarked.class);

    @ArchTest
    static final ArchRule pureLogicPackagesMustNotDependOnLibGdx = noClasses()
        .that().resideInAnyPackage("game.domain..", "game.engine..", "game.scenario..", "game.terrain..")
        .should().dependOnClassesThat().resideInAPackage("com.badlogic.gdx..");

    @ArchTest
    static final ArchRule libGdxDependentClassesMustResideInPlatformOrScreens = noClasses()
        .that().resideOutsideOfPackages("game.platform..", "game.screens..")
        .should().dependOnClassesThat().resideInAPackage("com.badlogic.gdx..");

    private static ArchCondition<JavaClass> resideInPackageWithPackageInfo() {
        return new ArchCondition<>("reside in package with package-info") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                var packagePath = javaClass.getPackageName().replace('.', '/');
                var relativePath = Path.of("src", "main", "java", packagePath, "package-info.java");
                var hasPackageInfo = Files.exists(relativePath)
                    || Files.exists(Path.of("core").resolve(relativePath));
                var message = javaClass.getFullName() + " is in package " + javaClass.getPackageName()
                    + " which must declare package-info.java";
                events.add(new SimpleConditionEvent(javaClass, hasPackageInfo, message));
            }
        };
    }
}
