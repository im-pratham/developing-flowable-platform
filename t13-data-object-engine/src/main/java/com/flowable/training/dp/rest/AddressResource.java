package com.flowable.training.dp.rest;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.rest.api.DataResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flowable.training.dp.dataobject.Address;
import com.flowable.training.dp.service.AddressService;

@RestController
public class AddressResource {

    private final AddressService addressService;

    public AddressResource(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Gets a single address by its ID.
     * @param addressId The ID of the address.
     * @return A single address with the given ID or null if there is no such address.
     */
    @GetMapping(value = "/api/addresses/{addressId}", produces = "application/json")
    public Address getAddressById(@PathVariable String addressId) {
        return addressService.getAddressById(addressId);
    }

    /**
     * Gets all addresses currently stored in the database that satisfy the given criteria.
     * This should usually be indexed - here it hits the DB every time.
     * @return A list of addresses that satisfy the criteria
     */
    @GetMapping(value = "/api/addresses", produces = "application/json")
    public DataResponse<Address> findAddresses(@RequestParam(required = false) String searchText) {
        List<Address> addresses;
        if(StringUtils.isEmpty(searchText)) {
            addresses = addressService.getAllAddresses();
        } else {
            addresses = addressService.findAddresses(searchText);
        }
        DataResponse<Address> dataResponse = new DataResponse<>();
        dataResponse.setData(addresses);
        dataResponse.setSize(addresses.size());
        dataResponse.setStart(0);
        dataResponse.setTotal(addresses.size());
        dataResponse.setOrder("asc");
        return dataResponse;
    }

}
