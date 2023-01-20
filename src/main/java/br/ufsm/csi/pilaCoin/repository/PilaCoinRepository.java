package br.ufsm.csi.pilaCoin.repository;


import br.ufsm.csi.pilaCoin.model.PilaCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilaCoinRepository extends JpaRepository <PilaCoin,Long> {
}
