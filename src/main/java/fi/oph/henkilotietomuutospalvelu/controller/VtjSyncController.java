package fi.oph.henkilotietomuutospalvelu.controller;

import fi.oph.henkilotietomuutospalvelu.dto.HetuDto;
import fi.oph.henkilotietomuutospalvelu.service.HetuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;

@RestController
@Api(value = "/vtj", description = "VTJ register endpoints")
@Slf4j
@RequestMapping("/vtj")
@RequiredArgsConstructor
public class VtjSyncController {

    private static final int MAX_HANDLED = 5000;

    private final HetuService hetuService;

    @PreAuthorize("hasAnyRole('APP_HENKILOTIETOMUUTOS_REKISTERINPITAJA', 'APP_HENKILOTIETOMUUTOS_PALVELUKAYTTAJA')")
    @RequestMapping(
            value = "/hetus",
            method = RequestMethod.POST,
            consumes = "application/json",
            produces = "application/json")
    @ApiOperation(
            value = "Update VTJ hetu register",
            notes = "Create a file containing added and removed hetus. " +
                    "Afterwards send the file to Tieto SFTP server. Forced limit of " + MAX_HANDLED + " total hetus.")
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Content is malformed"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 200, message = "Operation went ok")})
    public void updateHetus(@RequestBody HetuDto hetuDto) {
        if (Stream.of(hetuDto.getAddedHetus().stream(), hetuDto.getRemovedHetus().stream()).count() > MAX_HANDLED) {
            throw new IllegalArgumentException(String.format("Max allowed operations %d", MAX_HANDLED));
        }

        this.hetuService.updateHetusToDb(hetuDto);
    }

}
