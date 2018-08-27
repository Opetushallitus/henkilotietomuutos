package fi.oph.henkilotietomuutospalvelu.model.tietoryhma;

import fi.oph.henkilotietomuutospalvelu.dto.KoodiMetadataDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.Koodisto;
import fi.oph.henkilotietomuutospalvelu.service.KoodistoService;
import fi.vm.sade.oppijanumerorekisteri.dto.HenkiloDto;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Optional;

@RequiredArgsConstructor
class TestTietoryhmaContextImpl implements Tietoryhma.Context {

    private final HenkiloDto henkiloDto;
    private Optional<KoodistoService> koodistoService = Optional.empty();

    public TestTietoryhmaContextImpl(HenkiloDto henkiloDto, KoodistoService koodistoService) {
        this.henkiloDto = henkiloDto;
        this.koodistoService = Optional.of(koodistoService);
    }

    @Override
    public HenkiloDto getCurrentHenkilo() {
        return henkiloDto;
    }

    @Override
    public Optional<String> getPostitoimipaikka(String postinumero, String kieli) {
        return koodistoService.flatMap(service -> service.list(Koodisto.POSTI).stream()
                .filter(koodi -> postinumero.equals(koodi.getKoodiArvo()))
                .findFirst()
                .flatMap(koodi -> koodi.getMetadata().stream()
                        .filter(metadata -> metadata.getKieli().trim().toLowerCase().equals(kieli))
                        .map(KoodiMetadataDto::getNimi)
                        .filter(StringUtils::hasLength)
                        .findFirst()));
    }

    @Override
    public Optional<String> getMaa(String maakoodi, String kieli) {
        return koodistoService.flatMap(service -> service.list(Koodisto.MAAT_JA_VALTIOT_2).stream()
                .filter(koodi -> maakoodi.equals(koodi.getKoodiArvo()))
                .findFirst()
                .flatMap(koodi -> koodi.getMetadata().stream()
                        .filter(metadata -> metadata.getKieli().trim().toLowerCase().equals(kieli))
                        .map(KoodiMetadataDto::getNimi)
                        .filter(StringUtils::hasLength)
                        .findFirst()));
    }

}
