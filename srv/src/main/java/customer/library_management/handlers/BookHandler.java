package customer.library_management.handlers;

import cds.gen.libraryservice.LibraryService_;
import cds.gen.my.library.Book;
import cds.gen.my.library.Book_;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ServiceName(LibraryService_.CDS_NAME)
public class BookHandler implements EventHandler {
    @Autowired
    PersistenceService persistenceService;

    @Before(event = "CREATE", entity = Book_.CDS_NAME)
    public void beforeCreateBook(Book book) {
        if(book.getTitle() == null || "".equals(book.getTitle())) {
            throw new RuntimeException("Title is mandatory");
        }
        if(book.getAuthor() == null || "".equals(book.getAuthor())) {
            throw new RuntimeException("Author is mandatory");
        }

        if (book.getStock() != null && book.getStock() < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }
    }

    @On(event = "CREATE", entity = "LibraryService.Books")
    public void onCreateBook(Book payload, CdsCreateEventContext context) {
        String title = payload.getTitle();
        String author = payload.getAuthor();

        List<Book> existing = persistenceService.run(
                Select.from(Book_.class)
                        .where(b -> b.title().eq(title).and(b.author().eq(author)))
        ).list();

        if (!existing.isEmpty()) {
            Book found = existing.get(0);
            int existingStock = found.getStock() == null ? 0 : found.getStock();
            int addStock = payload.getStock() == null ? 0 : payload.getStock();
            found.setStock(existingStock + addStock);

            persistenceService.run(
                    Update.entity(Book_.class)
                            .data(found)
                            .where(b -> b.ID().eq(found.getId()))
            );

            List<Book> updated = persistenceService.run(
                    Select.from(Book_.class).where(b -> b.ID().eq(found.getId()))
            ).list();

            context.setResult(updated);
            context.setCompleted();
        }
    }
}
