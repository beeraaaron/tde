package tde.db;

import tde.importers.XMLHandler;
import tde.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleDataService implements DataService {
    private List<Country> countries = new ArrayList<>();
    private List<Canton> cantons = new ArrayList<>();
    private List<District> districts = new ArrayList<>();
    private List<Municipality> municipalities = new ArrayList<>();

    private List<Address> addresses = new ArrayList<>();

    @Override
    public void storeTerritoriesFromLoader(XMLHandler handler) {
        countries = handler.getCountries();
        cantons = handler.getCantons();
        districts = handler.getDistricts();
        municipalities = handler.getMunicipalities();
    }

    @Override
    public void storeAddressesFromLoader(List<Address> someAddresses) {
        addresses = someAddresses;
    }

    @Override
    public List<Address> getAllAddresses() {
        return addresses;
    }

    @Override
    public List<Country> getAllCountries() {
        return countries;
    }

    @Override
    public List<Canton> getAllCantons() {
        return cantons;
    }

    @Override
    public List<District> getAllDistricts() {
        return districts;
    }

    @Override
    public List<Municipality> getAllMunicipalities() {
        return municipalities;
    }

    @Override
    public Optional<Country> getCountryByName(String name) {
        return countries.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }
}
