package org.scottishtecharmy.wishaw.service;

import lombok.RequiredArgsConstructor;
import org.scottishtecharmy.wishaw.entity.Centre;
import org.scottishtecharmy.wishaw.repository.CentreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CentreService {

    private final CentreRepository centreRepository;

    @Transactional(readOnly = true)
    public List<Centre> findAll() {
        return centreRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Centre findById(Long id) {
        return centreRepository.findById(id).orElse(null);
    }

    @Transactional
    public Centre save(Centre centre) {
        return centreRepository.save(centre);
    }

    @Transactional
    public void delete(Long id) {
        centreRepository.deleteById(id);
    }
}
