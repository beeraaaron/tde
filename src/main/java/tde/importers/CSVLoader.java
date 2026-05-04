package tde.importers;

import tde.model.Address;
import tde.model.Coordinates;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CSVLoader {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public List<Address> readAddressData(File file, String canton) throws IOException {
        var fs = new FileInputStream(file);
        var sr = new InputStreamReader(fs);

        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(sr)) {
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                rows.add(line.split(";"));
                line = reader.readLine();
            }
        }

        return rows.stream()
                .filter(s -> Objects.equals(s[11], canton))
                .map(this::toAddress)
                .filter(Objects::nonNull)
                .toList();
    }

    private Address toAddress(String[] strings) {
        Address address = null;
        try {
            address = new Address(
                    new Coordinates(Double.parseDouble(strings[15]), Double.parseDouble(strings[16]), 0),
                    LocalDate.parse(strings[14], formatter),
                    strings[5],
                    Boolean.parseBoolean(strings[13]),
                    strings[12],
                    strings[6],
                    "Haus",
                    strings[11],
                    strings[10],
                    strings[4],
                    strings[8]
            );
        } catch (RuntimeException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return address;
    }
}
