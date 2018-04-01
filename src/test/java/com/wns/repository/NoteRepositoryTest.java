package com.wns.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import wns.cashman.CashManApplication;
import wns.cashman.enums.Denomination;
import wns.cashman.model.BankNote;
import wns.cashman.repository.NoteRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { CashManApplication.class })
@DataJpaTest
public class NoteRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private NoteRepository noteRepository;

	@Test
	public void testFindByType() {
		entityManager.persist(new BankNote(Denomination.FIFTY, 10));
		BankNote bankNote = noteRepository.findByType(Denomination.FIFTY);
		assertEquals(10, bankNote.getNumber());
	}
	
	@Test
	public void testCountByType() {
		entityManager.persist(new BankNote(Denomination.FIFTY, 10));
		int number = noteRepository.countByType(Denomination.FIFTY);
		assertEquals(1, number);
	}

}
