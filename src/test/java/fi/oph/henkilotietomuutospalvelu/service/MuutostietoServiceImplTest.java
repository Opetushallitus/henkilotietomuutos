package fi.oph.henkilotietomuutospalvelu.service;

import com.google.common.collect.Lists;
import fi.oph.henkilotietomuutospalvelu.dto.MuutostietoDto;
import fi.oph.henkilotietomuutospalvelu.dto.type.MuutosType;
import fi.oph.henkilotietomuutospalvelu.model.tietoryhma.*;
import fi.oph.henkilotietomuutospalvelu.repository.HenkiloMuutostietoRepository;
import fi.oph.henkilotietomuutospalvelu.repository.TiedostoRepository;
import fi.oph.henkilotietomuutospalvelu.service.impl.MuutostietoServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
public class MuutostietoServiceImplTest {
    @InjectMocks
    private MuutostietoServiceImpl muutostietoService;

    @Mock
    private FileService fileService;

    @Mock
    private MuutostietoParseService muutostietoParseService;

    @Mock
    private MuutostietoHandleService muutostietoHandleService;

    @Mock
    private HenkiloMuutostietoRepository henkiloMuutostietoRepository;

    @Mock
    private TiedostoRepository tiedostoRepository;

    @Captor
    private ArgumentCaptor<List<MuutostietoDto>> listArgumentCaptor;

    @Test
    public void handleFileLineNumberingIfNextPart() throws Exception {
        String fileName = "12345_20180120OPHREK_011.MTT";
        given(this.tiedostoRepository.findByFileName(eq(fileName))).willReturn(Optional.empty());

        MuutostietoDto muutostietoDto = MuutostietoDto.builder()
                .muutosType(MuutosType.UUSI)
                .build();
        given( this.fileService.readFile(any())).willReturn(Lists.newArrayList("1"));
        given(this.muutostietoParseService.deserializeMuutostietoLine(eq("1"))).willReturn(muutostietoDto);

        int lastLineNumber = 100;
        ReflectionTestUtils.invokeMethod(muutostietoService, "handleFile", "C:\\Users\\user\\henkilotietomuutos\\import\\12345_20180120OPHREK_011.MTT_001.PART", lastLineNumber);

        verify(this.muutostietoHandleService).importUnprocessedMuutostiedotToDb(this.listArgumentCaptor.capture(), eq(fileName));

        verify(this.fileService, never()).splitFile(any());
        assertThat(this.listArgumentCaptor.getValue())
                .extracting(MuutostietoDto::getRivi)
                .containsExactlyInAnyOrder(101);
    }

    @Test
    public void squashMultipartTietoryhmatWithNothingToSquash() {
        MuutostietoDto m = MuutostietoDto.builder().build();
        MuutostietoDto ma = MuutostietoDto.builder().build();
        MuutostietoDto mb = MuutostietoDto.builder().build();

        List<MuutostietoDto> muutostiedot = Arrays.asList(m, ma, mb);
        List<MuutostietoDto> squashedMuutostiedot = MuutostietoServiceImpl.squashMultipartMuutostiedot(muutostiedot);

        assertThat(squashedMuutostiedot).hasSize(3);
    }

    @Test
    public void squashMultipartTietoryhmatWithOneThingToSquash() {
        List<Tietoryhma> t = new ArrayList<>();
        Kotikunta kotikunta = Kotikunta.builder().build();
        t.add(kotikunta);

        Ammatti ammatti = Ammatti.builder().build();
        Aidinkieli aidinkieli = Aidinkieli.builder().build();

        MuutostietoDto m = MuutostietoDto.builder().muutosType(MuutosType.UUSI).tietoryhmat(t).build();
        MuutostietoDto ma = MuutostietoDto.builder().muutosType(MuutosType.JATKETTU).tietoryhmat(Arrays.asList(ammatti, aidinkieli)).build();
        MuutostietoDto mb = MuutostietoDto.builder().muutosType(MuutosType.UUSI).build();

        List<MuutostietoDto> muutostiedot = new ArrayList<>(Arrays.asList(m, ma, mb));
        List<MuutostietoDto> squashedMuutostiedot = MuutostietoServiceImpl.squashMultipartMuutostiedot(muutostiedot);

        assertThat(squashedMuutostiedot).hasSize(2).doesNotContain(ma);
        m = squashedMuutostiedot.get(0);
        assertThat(m.getTietoryhmat()).hasSize(3).containsExactlyInAnyOrder(kotikunta, ammatti, aidinkieli);
        assertThat(mb.getTietoryhmat()).hasSize(0);
    }

    @Test
    public void squashMultipartTietoryhmatWithOneVeryLongThingToSquash() {
        List<Tietoryhma> t = new ArrayList<>();
        Kotikunta kotikunta = Kotikunta.builder().build();
        t.add(kotikunta);

        List<Tietoryhma> ta = new ArrayList<>();
        Ammatti ammatti = Ammatti.builder().build();
        Aidinkieli aidinkieli = Aidinkieli.builder().build();
        ta.add(ammatti);
        ta.add(aidinkieli);

        Postiosoite postiosoite = Postiosoite.builder().build();

        MuutostietoDto m = MuutostietoDto.builder().muutosType(MuutosType.UUSI).tietoryhmat(t).build();
        MuutostietoDto ma = MuutostietoDto.builder().muutosType(MuutosType.JATKETTU).tietoryhmat(ta).build();
        MuutostietoDto mb = MuutostietoDto.builder().muutosType(MuutosType.JATKETTU).tietoryhmat(Lists.newArrayList(postiosoite)).build();
        MuutostietoDto mc = MuutostietoDto.builder().muutosType(MuutosType.UUSI).build();

        List<MuutostietoDto> muutostiedot = new ArrayList<>(Arrays.asList(m, ma, mb, mc));
        List<MuutostietoDto> squashedMuutostiedot = MuutostietoServiceImpl.squashMultipartMuutostiedot(muutostiedot);

        assertThat(squashedMuutostiedot).hasSize(2).doesNotContain(ma, mb);
        m = squashedMuutostiedot.get(0);
        assertThat(m.getTietoryhmat()).hasSize(4).containsExactlyInAnyOrder(kotikunta, ammatti, aidinkieli, postiosoite);
        assertThat(mc.getTietoryhmat()).hasSize(0);
    }

}
