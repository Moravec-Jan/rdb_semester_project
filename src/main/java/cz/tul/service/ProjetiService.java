package cz.tul.service;

import cz.tul.model.Projeti;
import cz.tul.repository.AutoRepository;
import cz.tul.repository.BranaRepository;
import cz.tul.repository.ProjetiRepository;
import cz.tul.repository.RidicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjetiService {

    @Autowired
    ProjetiRepository projetiRepository;
    @Autowired
    BranaRepository branaRepository;
    @Autowired
    AutoRepository autoRepository;
    @Autowired
    RidicRepository ridicRepository;

    public void saveWholeRecords(List<Projeti> projeti){
        if (projeti != null) {
            projeti.forEach(projeti1 -> {
                ridicRepository.save(projeti1.getCrp_ridic());
                autoRepository.save(projeti1.getSpz_auto());
                branaRepository.save(projeti1.getId_brana());
                projetiRepository.save(projeti1);
            });
        }
    }

}
