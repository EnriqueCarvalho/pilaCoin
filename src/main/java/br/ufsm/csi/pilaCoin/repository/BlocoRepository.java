package br.ufsm.csi.pilaCoin.repository;


import br.ufsm.csi.pilaCoin.model.Bloco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlocoRepository extends JpaRepository<Bloco, Long> {
}
