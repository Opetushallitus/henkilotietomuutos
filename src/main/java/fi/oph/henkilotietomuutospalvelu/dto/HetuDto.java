package fi.oph.henkilotietomuutospalvelu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HetuDto {

    private List<String> addedHetus = new ArrayList<>();
    private List<String> removedHetus = new ArrayList<>();

}
