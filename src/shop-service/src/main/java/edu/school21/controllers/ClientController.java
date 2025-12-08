package edu.school21.controllers;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.ClientDto;
import edu.school21.entity.Client;
import edu.school21.services.ClientService;
import edu.school21.utils.MapperUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final MapperUtil mapperUtil;
    private final ClientService clientService;

    @GeneralApiResponses(summary = "Get all clients with pagination")
    @GetMapping
    public List<ClientDto> getClients(
            @RequestParam(required = false)
            @Min(value = 1, message = "must be greater than 0")
            @Max(value = 25, message = "must be less than 26")
            Integer limit,
            @RequestParam(required = false)
            @Min(value = 0, message = "must be positive")
            Integer offset) {
        if (limit == null || offset == null) {
            return clientService.findAll().stream()
                    .map(mapperUtil::mapToClientDto)
                    .toList();
        }
        return clientService.findAll(PageRequest.of(offset, limit)).stream()
                .map(mapperUtil::mapToClientDto)
                .toList();
    }

    @GeneralApiResponses(summary = "Get all clients by name and surname")
    @GetMapping("/search")
    public List<ClientDto> findClientsByNameAndSurname(
            @RequestParam("name")
            @NotBlank(message = "cannot be empty") String name,
            @RequestParam("surname")
            @NotBlank(message = "cannot be empty") String surname) {
        List<Client> clients = clientService.findClientsByNameAndSurname(name, surname);
        return clients.stream().map(mapperUtil::mapToClientDto).toList();
    }

    @GeneralApiResponses(summary = "Create client")
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto createClient(@Valid @RequestBody ClientDto clientDto) {
        Client client = mapperUtil.mapToClient(clientDto);
        client = clientService.saveClient(client);
        return mapperUtil.mapToClientDto(client);
    }

    @GeneralApiResponses(summary = "Update client by id")
    @PatchMapping("/{id}")
    public ClientDto patchUpdateClient(
            @PathVariable("id") UUID id,
            @RequestBody ClientDto clientDto) {
        clientDto.setId(id);
        Client client = mapperUtil.mapToClient(clientDto);
        client = clientService.patchUpdateClient(client);
        return mapperUtil.mapToClientDto(client);
    }

    @GeneralApiResponses(summary = "Create or update client by id")
    @PutMapping("/{id}")
    public ClientDto putUpdateClient(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ClientDto clientDto) {
        Client client = mapperUtil.mapToClient(clientDto);
        client = clientService.putUpdateClient(id, client);
        return mapperUtil.mapToClientDto(client);
    }

    @GeneralApiResponses(summary = "Delete client by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable("id") UUID id) {
        clientService.deleteClient(id);
    }
}
