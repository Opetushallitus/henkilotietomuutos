package fi.oph.henkilotietomuutospalvelu.service.impl;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@EqualsAndHashCode
public class MuutostietoLine {
    public final int lineNumber;
    public final String content;
}
