
DROP VIEW IF EXISTS localized_LibraryService_Books;
DROP VIEW IF EXISTS localized_my_library_Book;
DROP VIEW IF EXISTS LibraryService_Loans;
DROP VIEW IF EXISTS LibraryService_Members;
DROP VIEW IF EXISTS LibraryService_Books_texts;
DROP VIEW IF EXISTS LibraryService_Books;
DROP TABLE IF EXISTS cds_outbox_Messages;
DROP TABLE IF EXISTS my_library_Loan;
DROP TABLE IF EXISTS my_library_Member;
DROP TABLE IF EXISTS my_library_Book_texts;
DROP TABLE IF EXISTS my_library_Book;

CREATE TABLE my_library_Book (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  title NVARCHAR(255),
  author NVARCHAR(255),
  stock INTEGER,
  PRIMARY KEY(ID)
);

CREATE TABLE my_library_Book_texts (
  locale NVARCHAR(14) NOT NULL,
  ID NVARCHAR(36) NOT NULL,
  title NVARCHAR(255),
  PRIMARY KEY(locale, ID)
);

CREATE TABLE my_library_Member (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  name NVARCHAR(255),
  email NVARCHAR(255),
  PRIMARY KEY(ID)
);

CREATE TABLE my_library_Loan (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP(7),
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP(7),
  modifiedBy NVARCHAR(255),
  book_ID NVARCHAR(36),
  member_ID NVARCHAR(36),
  loanDate DATE,
  dueDate DATE,
  returnDate DATE,
  fineAmount INTEGER,
  PRIMARY KEY(ID)
);

CREATE TABLE cds_outbox_Messages (
  ID NVARCHAR(36) NOT NULL,
  timestamp TIMESTAMP(7),
  target NVARCHAR(255),
  msg NCLOB,
  attempts INTEGER DEFAULT 0,
  "PARTITION" INTEGER DEFAULT 0,
  lastError NCLOB,
  lastAttemptTimestamp TIMESTAMP(7),
  status NVARCHAR(23),
  task NVARCHAR(255),
  appid NVARCHAR(255),
  PRIMARY KEY(ID)
);

CREATE VIEW LibraryService_Books AS SELECT
  Book_0.ID,
  Book_0.createdAt,
  Book_0.createdBy,
  Book_0.modifiedAt,
  Book_0.modifiedBy,
  Book_0.title,
  Book_0.author,
  Book_0.stock
FROM my_library_Book AS Book_0;

CREATE VIEW LibraryService_Books_texts AS SELECT
  texts_0.locale,
  texts_0.ID,
  texts_0.title
FROM my_library_Book_texts AS texts_0;

CREATE VIEW LibraryService_Members AS SELECT
  Member_0.ID,
  Member_0.createdAt,
  Member_0.createdBy,
  Member_0.modifiedAt,
  Member_0.modifiedBy,
  Member_0.name,
  Member_0.email
FROM my_library_Member AS Member_0;

CREATE VIEW LibraryService_Loans AS SELECT
  Loan_0.ID,
  Loan_0.createdAt,
  Loan_0.createdBy,
  Loan_0.modifiedAt,
  Loan_0.modifiedBy,
  Loan_0.book_ID,
  Loan_0.member_ID,
  Loan_0.loanDate,
  Loan_0.dueDate,
  Loan_0.returnDate,
  Loan_0.fineAmount
FROM my_library_Loan AS Loan_0;

CREATE VIEW localized_my_library_Book AS SELECT
  L_0.ID,
  L_0.createdAt,
  L_0.createdBy,
  L_0.modifiedAt,
  L_0.modifiedBy,
  coalesce(localized_1.title, L_0.title) AS title,
  L_0.author,
  L_0.stock
FROM (my_library_Book AS L_0 LEFT JOIN my_library_Book_texts AS localized_1 ON localized_1.ID = L_0.ID AND localized_1.locale = @locale);

CREATE VIEW localized_LibraryService_Books AS SELECT
  Book_0.ID,
  Book_0.createdAt,
  Book_0.createdBy,
  Book_0.modifiedAt,
  Book_0.modifiedBy,
  Book_0.title,
  Book_0.author,
  Book_0.stock
FROM localized_my_library_Book AS Book_0;
