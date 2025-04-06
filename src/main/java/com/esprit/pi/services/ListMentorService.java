package com.esprit.pi.services;

import com.esprit.pi.entities.ListMentor;
import com.esprit.pi.repositories.ListMentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ListMentorService {

    @Autowired
    private ListMentorRepository listMentorRepository;

    // Create
    public ListMentor createListMentor(ListMentor listMentor) {
        return listMentorRepository.save(listMentor);
    }

    // Read
    public List<ListMentor> getAllListMentors() {
        return listMentorRepository.findAll();
    }

    public Optional<ListMentor> getListMentorById(Long id) {
        return listMentorRepository.findById(id);
    }

    public List<ListMentor> getListMentorsByMentorId(Long mentorId) {
        return listMentorRepository.findByMentorId(mentorId);
    }

    public List<ListMentor> getListMentorsByHackathonId(Long hackathonId) {
        return listMentorRepository.findByHackathonId(hackathonId);
    }

    // Update
    public ListMentor updateListMentor(ListMentor listMentor) {
        return listMentorRepository.save(listMentor);
    }

    // Delete
    public void deleteListMentor(Long id) {
        listMentorRepository.deleteById(id);
    }
}