package com.medicine.webscraper.repository;

import java.util.UUID;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.medicine.webscraper.model.Medicine;

@Repository
public interface MedicineRepo extends ElasticsearchRepository<Medicine, UUID> {

}
