package com.zetenkastel.main;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

class ArchitectureRulesTest {

    private final JavaClasses classes = new ClassFileImporter().importPackages("com.zetenkastel");

    @Test
    void domain_must_not_depend_on_ui_app_or_adapter() {
        noClasses()
                .that().resideInAPackage("..core.domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..core.ui..", "..core.app..", "..core.adapter..")
                .check(classes);
    }

    @Test
    void ui_must_not_depend_on_adapter() {
        noClasses()
                .that().resideInAPackage("..core.ui..")
                .should().dependOnClassesThat()
                .resideInAPackage("..core.adapter..")
                .check(classes);
    }
}
