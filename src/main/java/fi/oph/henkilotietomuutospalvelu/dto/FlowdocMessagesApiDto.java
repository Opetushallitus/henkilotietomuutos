package fi.oph.henkilotietomuutospalvelu.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlowdocMessagesApiDto {
    String flow_token;
    String event;
    String content;
    String tags;
}
