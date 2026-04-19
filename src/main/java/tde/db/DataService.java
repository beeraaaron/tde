package tde.db;

import tde.importers.XMLHandler;
import tde.model.Address;
import tde.model.Country;

import java.util.List;
import java.util.Optional;

/**
 * All access to a persistent Layer goes through this interface.
 */
public interface DataService {
    /**
     * Retrieve a single list of all countries.
     * @return list of countries.
     */
    List<Country> getAllCountries();

    /**
     * Retrieve a single country by its name. This query will return immediately upon the first country
     * that matches with its name.
     * @param name the countries name
     * @return the country found (or null if nothing was found)
     */
    Optional<Country> getCountryByName(String name);

    /**
     * Retrieve a list of all addresses that are available.
     * @return a list of addresses, never null but might be an empty list.
     */
    List<Address> getAllAddresses();

    // TODO
    void storeTerritoriesFromLoader(XMLHandler handler);

    // TODO
    void storeAddressesFromLoader(List<Address> addresses);
}
