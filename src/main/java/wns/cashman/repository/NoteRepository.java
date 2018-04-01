package wns.cashman.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import wns.cashman.enums.Denomination;
import wns.cashman.model.BankNote;

@Repository
public interface NoteRepository extends JpaRepository<BankNote, Long> {

	BankNote findByType(Denomination denomination);

	int countByType(Denomination denomination);
	
}

