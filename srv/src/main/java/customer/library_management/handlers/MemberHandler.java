package customer.library_management.handlers;

import cds.gen.libraryservice.LibraryService_;
import cds.gen.my.library.Member;
import cds.gen.my.library.Member_;
import com.sap.cds.ql.Select;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
@ServiceName(LibraryService_.CDS_NAME)
public class MemberHandler implements EventHandler {

    @Autowired
    PersistenceService persistenceService;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
    );

    @Before(event = "CREATE", entity = Member_.CDS_NAME)
    public void beforeCreateMember(Member member) {
        if(member.getName() == null || "".equals(member.getName())) {
            throw new RuntimeException("Name is mandatory");
        }

        if(member.getEmail() == null || "".equals(member.getEmail())) {
            throw new RuntimeException("Email is mandatory");
        }

        String email = member.getEmail();

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new RuntimeException("Invalid email format: " + email);
        }

        List<Member> existing = persistenceService.run(
                Select.from(Member_.class)
                        .where(m -> m.email().eq(email))
        ).list();
        if (!existing.isEmpty()) {
            throw new RuntimeException("Member with email " + email + " already exists");
        }
    }
}
