package fi.oph.henkilotietomuutospalvelu.service;

import fi.oph.henkilotietomuutospalvelu.dto.HetuDto;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

public interface HetuService {


    void updateHetusToDb(HetuDto hetuDto);

    /**
     * Updates
     * @return added and removed hetus mixed
     */
    Set<String> updateHetusToVtj();

}
