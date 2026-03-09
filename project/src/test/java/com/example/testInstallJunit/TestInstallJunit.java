package com.example.testInstallJunit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestInstallJunit {

    @Test
    public void test_install_junit(){
        assertEquals(1, 2, "L'installation fonctionne");
    }

}
