package com.abastecimiento.sudab.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.abastecimiento.sudab.Model.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {}