package org.comparision;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class FileSorter {
    public static void main(String[] args) {
        String inputFilePath1 = "file1.txt";
        String inputFilePath2 = "file2.txt";
        String delimiter = Pattern.quote("\\|"); // Use Pattern.quote to treat the delimiter as a literal string

        // Sort data for first file
        Pair file1Data = readAndSortData(inputFilePath1, delimiter);
        writeSortedDataToFile(file1Data.getColumnNames(), file1Data.getData(), "sorted_data1.txt");

        // Sort data for second file
        Pair file2Data = readAndSortData(inputFilePath2, delimiter);
        writeSortedDataToFile(file2Data.getColumnNames(), file2Data.getData(), "sorted_data2.txt");
    }

    private static Pair readAndSortData(String filePath, String delimiter) {
        String[] columnNames = null;
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(delimiter, -1); // Use -1 as the limit to keep empty columns
                if (lineCount == 0) {
                    // First row contains column names with escape characters, so remove them
                    columnNames = removeEscapeCharacters(values);
                } else {
                    data.add(values);
                }
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (columnNames == null || data.isEmpty()) {
            System.out.println("Empty file or missing column names.");
        } else {
            // Sort the data based on the unique index
            sortData(data, columnNames);
        }

        return new Pair(columnNames, data);
    }

    private static void sortData(List<String[]> data, String[] columnNames) {
        // Custom comparator to sort based on the unique index (multiple columns)
        Comparator<String[]> indexComparator = (row1, row2) -> {
            for (String columnName : columnNames) {
                int columnIndex = getColumnIndex(columnNames, columnName);
                int compareResult = row1[columnIndex].compareTo(row2[columnIndex]);
                if (compareResult != 0) {
                    return compareResult;
                }
            }
            return 0;
        };

        data.sort(indexComparator);
    }

    private static void writeSortedDataToFile(String[] columnNames, List<String[]> data, String sortedFilePath) {
        if (columnNames == null || data == null || data.isEmpty()) {
            System.out.println("No data to write or missing column names.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(sortedFilePath))) {
            // Write column names to the sorted file
            bw.write(String.join("|", columnNames));
            bw.newLine();

            // Write sorted data to the sorted file
            for (String[] row : data) {
                bw.write(String.join("|", row));
                bw.newLine();
            }
            System.out.println("Data sorted and written to " + sortedFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getColumnIndex(String[] columnNames, String columnName) {
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    private static String[] removeEscapeCharacters(String[] values) {
        String[] cleanedValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            cleanedValues[i] = values[i].replaceAll("\\\\", "");
        }
        return cleanedValues;
    }

    // Custom Pair class to hold column names and data
    private static class Pair {
        private String[] columnNames;
        private List<String[]> data;

        public Pair(String[] columnNames, List<String[]> data) {
            this.columnNames = columnNames;
            this.data = data;
        }

        public String[] getColumnNames() {
            return columnNames;
        }

        public List<String[]> getData() {
            return data;
        }
    }
}
