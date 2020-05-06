package com.sw.multiplication.repository;

import com.sw.multiplication.domain.Multiplication;
import org.springframework.data.repository.CrudRepository;

public interface MultiplicationRepository extends CrudRepository<Multiplication,Long> {

}
