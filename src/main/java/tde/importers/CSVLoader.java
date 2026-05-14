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

    public List<Address> readAddressData(File file, String canton) {
        FileInputStream fs;
        try {
            fs = new FileInputStream(file);
        } catch (Exception e) {
            throw new RuntimeException("No valid File was selected.");
        }

        var sr = new InputStreamReader(fs);

        List<String[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(sr)) {
            reader.readLine();
            String line = reader.readLine();
            while (line != null) {
                rows.add(line.split(";"));
                line = reader.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error while reading CSV file " + file.getAbsolutePath());
            System.err.println(Arrays.toString(e.getStackTrace()));
            throw new RuntimeException("Error while reading CSV File. See Error log for details.");
        }

        return rows.stream()
                .filter(s -> Objects.equals(s[11], canton))
                .map(this::toAddress)
                .toList();
    }

    private Address toAddress(String[] strings) {
        return new Address(
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
    }
}
