package com.flowable.training.dp.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.flowable.dataobject.api.runtime.DataObjectInstance;
import com.flowable.dataobject.api.runtime.DataObjectRuntimeService;
import com.flowable.dataobject.api.runtime.datasource.db.DatabaseSchemaDataObjectInstance;
import com.flowable.training.dp.dataobject.Address;

@Service("addressService")
public class AddressService {

    private final DataObjectRuntimeService dataObjectRuntimeService;


    public AddressService(DataObjectRuntimeService dataObjectRuntimeService) {
        this.dataObjectRuntimeService = dataObjectRuntimeService;
    }

    /**
     * Returns a single data object instance with a given ID.
     * @param addressId The ID of the address.
     * @return A single address data object with the given ID or null if there is no such address.
     */
    public DataObjectInstance getAddressDataObjectById(String addressId) {
        return dataObjectRuntimeService.createDataObjectInstanceQueryByDefinitionKey(Address.DATA_OBJECT_KEY)
            .id(addressId)
            .singleResult();
    }


    /**
     * Gets a single address by its ID.
     * @param addressId The ID of the address.
     * @return A single address with the given ID or null if there is no such address.
     */
    public Address getAddressById(String addressId) {
        DataObjectInstance dataObjectInstance = dataObjectRuntimeService.createDataObjectInstanceQueryByDefinitionKey(Address.DATA_OBJECT_KEY)
            .id(addressId)
            .singleResult();

        Address address = extractAddressFromDataObject((DatabaseSchemaDataObjectInstance) dataObjectInstance);
        return address;
    }

    /**
     * Gets all addresses currently stored in the database.
     * @return A list of address DTOs
     */
    public List<Address> getAllAddresses() {
        List<Address> addresses = new ArrayList<>();
        List<DataObjectInstance> addressDOs = dataObjectRuntimeService.createDataObjectInstanceQueryByDefinitionKey(Address.DATA_OBJECT_KEY).list();
        for (DataObjectInstance dataObjectInstance : addressDOs) {
            Address address = extractAddressFromDataObject((DatabaseSchemaDataObjectInstance) dataObjectInstance);
            addresses.add(address);
        }
        return addresses;
    }

    /**
     * Finds all addresses that contain the given typed text.
     * Please note that this should usually happen with a custom index as this will hit the DB very often.
     * @param typedText The search text
     * @return A list of all addresses satisfying the criteria
     */
    public List<Address> findAddresses(String typedText) {
        List<Address> allAddresses = getAllAddresses();
        return allAddresses.stream().filter(
            address -> address.getName().contains(typedText) ||
                address.getCity().contains(typedText) ||
                address.getStreet().contains(typedText) ||
                address.getZipCode().contains(typedText))
            .collect(Collectors.toList());
    }


    /**
     * Extracts a single address from a DatabaseSchemaDataObjectInstance
     * @param dataObjectInstance The data object instance containing address information.
     * @return A single address.
     */
    private Address extractAddressFromDataObject(DatabaseSchemaDataObjectInstance dataObjectInstance) {
        String title = dataObjectInstance.getString(Address.FIELD_TITLE);
        String name = dataObjectInstance.getString(Address.FIELD_NAME);
        String street = dataObjectInstance.getString(Address.FIELD_STREET);
        String zipCode = dataObjectInstance.getString(Address.FIELD_ZIP_CODE);
        String city = dataObjectInstance.getString(Address.FIELD_CITY);
        String country = dataObjectInstance.getString(Address.FIELD_COUNTRY);
        Date lastUpdated = dataObjectInstance.getDate(Address.FIELD_LAST_UPDATED);

        Address address = new Address();
        address.setTitle(title);
        address.setName(name);
        address.setStreet(street);
        address.setZipCode(zipCode);
        address.setCity(city);
        address.setCountry(country);
        address.setLastUpdated(lastUpdated);

        return address;
    }
}
