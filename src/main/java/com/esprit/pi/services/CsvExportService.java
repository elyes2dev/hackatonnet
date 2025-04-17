// com/esprit/pi/services/CsvExportService.java
package com.esprit.pi.services;

import com.esprit.pi.entities.ListMentor;
import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

@Service
public class CsvExportService {

    @Autowired
    private ListMentorService listMentorService;

    public ByteArrayInputStream exportMentorsToCSV(Long hackathonId) throws IOException {
        List<ListMentor> mentors = listMentorService.getListMentorsByHackathonId(hackathonId);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(out))) {

            // Write header row
            csvWriter.writeNext(new String[]{
                    "Mentor ID",
                    "Mentor Name",
                    "Hackathon Name",
                    "Number of Teams"
            });

            // Write data rows
            for (ListMentor mentor : mentors) {
                csvWriter.writeNext(new String[]{
                        String.valueOf(mentor.getMentor().getId()),
                        mentor.getMentor().getName(), // Adjust based on your User entity
                        mentor.getHackathon().getTitle(),
                        String.valueOf(mentor.getNumberOfTeams())
                });
            }

            csvWriter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}