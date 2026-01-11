package customer.library_management.handlers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import cds.gen.libraryservice.ReturnBookContext;
import cds.gen.libraryservice.LibraryService_;
import cds.gen.libraryservice.ReturnBookResponse;
import cds.gen.my.library.*;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.ServiceName;

@Component
@ServiceName(LibraryService_.CDS_NAME)
public class LoanHandler implements EventHandler {

	@Autowired
	PersistenceService persistenceService;

	private static final int FINE_PER_DAY = 10;

	@Before(event = "CREATE", entity = "LibraryService.Loans")
	public void beforeCreateLoan(Loan loan) {
		LocalDate today = LocalDate.now();
		loan.setLoanDate(today);
		loan.setReturnDate(null);

		if (loan.getDueDate() == null) {
			throw new RuntimeException("Due date is mandatory for a loan");
		}

		if (loan.getDueDate().isBefore(today)) {
			throw new RuntimeException("Due date cannot be in the past");
		}

		if (loan.getDueDate().isBefore(today.plusDays(1))) {
			throw new RuntimeException("Due date must be at least 1 day after today");
		}

		if (loan.getDueDate().isAfter(today.plusDays(30))) {
			throw new RuntimeException("Due date cannot be more than 30 days from today");
		}

		String bookId = loan.getBookId();
		if (bookId == null || bookId.isEmpty()) {
			throw new RuntimeException("Book reference (book or bookId) is mandatory for a loan");
		}

		List<Book> bookList = persistenceService.run(
			Select.from(Book_.class).where(b -> b.ID().eq(bookId))
		).list();
		Book book = bookList.stream().findFirst().orElse(null);

		if(book == null) {
			throw new RuntimeException("Book with ID " + bookId + " does not exist");
		}

		String memberId = loan.getMemberId();
		if (memberId == null || memberId.isEmpty()) {
			throw new RuntimeException("Member reference (member or memberId) is mandatory for a loan");
		}

		List<Member> memberList = persistenceService.run(
			Select.from(Member_.class).where(m -> m.ID().eq(memberId))
		).list();
		Member member = memberList.stream().findFirst().orElse(null);

		if(member == null) {
			throw new RuntimeException("Member with ID " + memberId + " does not exist");
		}

		if(book.getStock() == null || book.getStock() <= 0) {
			throw new RuntimeException("Cannot loan a book that is out of stock");
		}
	}

	@On(event = "CREATE", entity = "LibraryService.Loans")
	public void onCreateLoan(Loan loan) {
		String bookId = loan.getBookId();
		Book book = persistenceService.run(
			Select.from(Book_.class).where(b -> b.ID().eq(bookId))
		).single(Book.class);

		book.setStock(book.getStock() - 1);
		persistenceService.run(Update.entity(Book_.class)
				.data(book)
				.where(b -> b.ID().eq(bookId)));
	}


	@Before(event = "returnBook")
	public void beforeReturnBook(ReturnBookContext context) {
		String loanId = context.getLoanId();
		if (loanId == null || loanId.isEmpty()) {
			throw new RuntimeException("Loan ID is mandatory to return a book");
		}

		String email = context.getEmail();
		if (email == null || email.isEmpty()) {
			throw new RuntimeException("Email is mandatory to return a book");
		}

		List<Loan> loanList = persistenceService.run(
				Select.from(Loan_.class)
						.where(l -> l.ID().eq(loanId))
		).list();

		Loan fetchedLoan = loanList.stream().findFirst().orElse(null);

		if (fetchedLoan == null) {
			throw new RuntimeException("Loan with ID " + loanId + " does not exist");
		}

		if (fetchedLoan.getReturnDate() != null) {
			throw new RuntimeException("Book already returned");
		}

		String memberId = fetchedLoan.getMemberId();
		Member member = persistenceService.run(
				Select.from(Member_.class).where(m -> m.ID().eq(memberId))
		).single(Member.class);

		if (member == null) {
			throw new RuntimeException("Member not found for this loan");
		}

		if (!email.equalsIgnoreCase(member.getEmail())) {
			throw new RuntimeException("Email does not match the member who borrowed this book. Access denied.");
		}
	}

	@On(event = "returnBook")
	public void returnBook(ReturnBookContext context) {
		String loanId = context.getLoanId();

		// Fetch loan
		Loan fetchedLoan = persistenceService.run(
				Select.from(Loan_.class).where(l -> l.ID().eq(loanId))
		).single(Loan.class);

		// Fetch book
		Book book = persistenceService.run(
				Select.from(Book_.class).where(b -> b.ID().eq(fetchedLoan.getBookId()))
		).single(Book.class);

		LocalDate today = LocalDate.now();
		LocalDate dueDate = fetchedLoan.getDueDate();

		long delayDays = ChronoUnit.DAYS.between(dueDate, today);
		delayDays = Math.max(delayDays, 0);

		Integer fine = (int) (delayDays * FINE_PER_DAY);

		persistenceService.run(
				Update.entity(Loan_.class)
						.data("returnDate", today)
						.data("fineAmount", fine)
						.where(l -> l.ID().eq(loanId))
		);

		persistenceService.run(
				Update.entity(Book_.class)
						.data("stock", book.getStock() + 1)
						.where(b -> b.ID().eq(fetchedLoan.getBookId()))
		);

		String message;
		if (fine == 0) {
			message = "Book returned successfully! No fine incurred.";
		} else {
			message = "Book returned successfully! Fine of ₹" + fine + " applied for " + delayDays + " days delay.";
		}

		ReturnBookResponse response = ReturnBookResponse.create();
		response.setMessage(message);
		response.setFine(fine);
		context.setResult(response);
	}

}