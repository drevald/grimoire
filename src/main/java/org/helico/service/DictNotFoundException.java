package org.helico.service;

public class DictNotFoundException extends RuntimeException {
    public DictNotFoundException(Long dictId) {
        super("Dict not found: " + dictId);
    }
}
