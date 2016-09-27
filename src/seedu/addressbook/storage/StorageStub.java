package seedu.addressbook.storage;

import seedu.addressbook.data.AddressBook;
import seedu.addressbook.storage.StorageFile.StorageOperationException;

public class StorageStub implements Storage {

    @Override
    public void save(AddressBook addressBook) throws StorageOperationException {
    }

    @Override
    public AddressBook load() throws StorageOperationException {
        return AddressBook.empty();
    }

    @Override
    public String getPath() {
        // TODO Auto-generated method stub
        return "STUB";
    }

}
