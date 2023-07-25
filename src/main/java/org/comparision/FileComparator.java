package org.comparision;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileComparator {
    public static void main(String[] args) {
        String file1Path = "sorted_data1.txt";
        String file2Path = "sorted_data2.txt";
        String outputFilePath = "compare_output.txt";

        try {
            List<String> file1Lines = readLinesFromFile(file1Path);
            List<String> file2Lines = readLinesFromFile(file2Path);

            List<String> columnNames = getColumnNames(file1Lines.get(0));
            List<Integer> mismatchedRows = new ArrayList<>();
            int totalMismatchedRecords = 0;
            Map<String, Integer> columnMismatchCounts = new HashMap<>();

            int numRowsToCheck = Math.min(file1Lines.size(), file2Lines.size());

            StringBuilder compareOutput = new StringBuilder();

            for (int i = 1; i < numRowsToCheck; i++) {
                String[] rowData1 = file1Lines.get(i).split("\\|");
                String[] rowData2 = file2Lines.get(i).split("\\|");

                List<Integer> mismatchedColumns = getMismatchedColumns(rowData1, rowData2);
                if (!mismatchedColumns.isEmpty()) {
                    mismatchedRows.add(i);
                    totalMismatchedRecords += mismatchedColumns.size();
                    compareOutput.append("Mismatch in Row ").append(i).append(", Columns:\n");

                    for (int columnIndex : mismatchedColumns) {
                        String columnName = columnNames.get(columnIndex);
                        String value1 = rowData1[columnIndex];
                        String value2 = rowData2[columnIndex];

                        compareOutput.append(columnName).append(": ")
                                .append("'").append(value1).append("' <-> '").append(value2).append("'\n");

                        // Increment the mismatch count for the column
                        columnMismatchCounts.put(columnName, columnMismatchCounts.getOrDefault(columnName, 0) + 1);
                    }
                    compareOutput.append("\n");
                }
            }

            // Check if there are any extra rows in file1
            for (int i = numRowsToCheck; i < file1Lines.size(); i++) {
                compareOutput.append("Extra row in File 1, Row ").append(i).append(": ").append(file1Lines.get(i)).append("\n");
            }

            // Check if there are any extra rows in file2
            for (int i = numRowsToCheck; i < file2Lines.size(); i++) {
                compareOutput.append("Extra row in File 2, Row ").append(i).append(": ").append(file2Lines.get(i)).append("\n");
            }

            if (compareOutput.length() == 0) {
                compareOutput.append("Files are identical. No mismatches found.");
            }

            // Write the comparison output to the file
            writeOutputToFile(outputFilePath, compareOutput.toString());
            // Print success message in console
            System.out.println("Successfully generated compare_output.txt");

            System.out.println("Total number of rows mismatched: " + mismatchedRows.size());
            // Print the mismatch count for each column
            for (Map.Entry<String, Integer> entry : columnMismatchCounts.entrySet()) {
                String columnName = entry.getKey();
                int mismatchCount = entry.getValue();
                System.out.println("Mismatch count for column '" + columnName + "': " + mismatchCount);
            }

        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }
    }

    private static List<String> readLinesFromFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    private static List<String> getColumnNames(String headerRow) {
        String[] columns = headerRow.split("\\|");
        List<String> columnNames = new ArrayList<>();
        for (String column : columns) {
            columnNames.add(column);
        }
        return columnNames;
    }

    private static List<Integer> getMismatchedColumns(String[] rowData1, String[] rowData2) {
        List<Integer> mismatchedColumns = new ArrayList<>();
        for (int i = 0; i < rowData1.length; i++) {
            if (!rowData1[i].equals(rowData2[i])) {
                mismatchedColumns.add(i);
            }
        }
        return mismatchedColumns;
    }

    private static void writeOutputToFile(String filePath, String output) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(output);
        }
    }
}
