package com.evlarus.ecomreturns.user.api;

import com.evlarus.ecomreturns.user.CurrentUserService;
import com.evlarus.ecomreturns.user.domain.Address;
import com.evlarus.ecomreturns.user.domain.User;
import com.evlarus.ecomreturns.user.infrastructure.AddressRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    private final AddressRepository addressRepository;
    private final CurrentUserService currentUserService;

    public AddressController(AddressRepository addressRepository, CurrentUserService currentUserService) {
        this.addressRepository = addressRepository;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public List<AddressResponse> list(Authentication authentication) {
        User user = currentUserService.resolve(authentication);
        return addressRepository.findByUserId(user.getId()).stream().map(AddressResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<AddressResponse> create(@Valid @RequestBody AddressCreateRequest request,
                                                   Authentication authentication) {
        User user = currentUserService.resolve(authentication);
        boolean isFirstAddress = addressRepository.findByUserId(user.getId()).isEmpty();

        Address address = new Address();
        address.setUser(user);
        address.setLine1(request.line1());
        address.setCity(request.city());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
        address.setDefault(isFirstAddress);
        addressRepository.save(address);

        return ResponseEntity.status(HttpStatus.CREATED).body(AddressResponse.from(address));
    }
}
