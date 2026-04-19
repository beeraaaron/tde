package tde.db;

import tde.importers.XMLHandler;
import tde.model.Address;
import tde.model.LandArea.Area;
import tde.model.Coordinates;
import tde.model.Country;
import tde.model.LandArea;

import java.util.List;
import java.util.Optional;

public class SimpleDataService implements DataService {

    private static final Country SIMPLE_COUNTRY = new Country("Sketch of Swiss Boundaries", 9_051_029, 4_129_069,
        new LandArea(List.of(
                new Area(List.of(
                        new Area.Boundaries(List.of(
                                new Coordinates(100, 300, 0),
                                new Coordinates(100, 800, 0),
                                new Coordinates(400, 800, 0),
                                new Coordinates(400, 300, 0)
                        )),
                        new Area.Boundaries(List.of(
                                new Coordinates(200, 500, 0),
                                new Coordinates(200, 600, 0),
                                new Coordinates(300, 600, 0),
                                new Coordinates(300, 500, 0)
                        ))
                )),
                new Area(List.of(
                        new Area.Boundaries(List.of(
                                new Coordinates(700, 100, 0),
                                new Coordinates(700, 200, 0),
                                new Coordinates(900, 200, 0),
                                new Coordinates(900, 100, 0)
                        ))
                ))
        ))
    );

    private static final Country SIMPLE_CH = new Country("Sketch of Swiss Boundaries", 9_051_029, 4_129_069,
        new LandArea(List.of(
            new Area(List.of(
                new Area.Boundaries(List.of(
                    new Coordinates(2487028, 1111657, 0),
                    new Coordinates(2501840, 1160792, 0),
                    new Coordinates(2567232, 1260505, 0),
                    new Coordinates(2685371, 1272427, 0),
                    new Coordinates(2687900, 1293743, 0),
                    new Coordinates(2687900, 1293743, 0),
                    new Coordinates(2687900, 1293743, 0),
                    new Coordinates(2757266, 1213900, 0),
                    new Coordinates(2802426, 1191862, 0),
                    new Coordinates(2825548, 1207036, 0),
                    new Coordinates(2831328, 1160069, 0),
                    new Coordinates(2802787, 1161153, 0),
                    new Coordinates(2805677, 1123580, 0),
                    new Coordinates(2744621, 1151037, 0),
                    new Coordinates(2724389, 1076975, 0),
                    new Coordinates(2675255, 1144173, 0),
                    new Coordinates(2634430, 1083116, 0),
                    new Coordinates(2576264, 1080226, 0),
                    new Coordinates(2530743, 1146340, 0),
                    new Coordinates(2505092, 1114548, 0)
                ))
            ))
        ))
    );

    private final List<Country> countries = List.of(SIMPLE_CH);
    private List<Address> addresses = List.of();

    @Override
    public void storeTerritoriesFromLoader(XMLHandler handler) {
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
    public Optional<Country> getCountryByName(String name) {
        return countries.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }
}
