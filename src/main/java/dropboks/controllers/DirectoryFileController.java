package dropboks.controllers;

import dropboks.PathResolver;
import dropboks.TransferFile;
import dropboks.dao.DirectoryMetadataDAO;
import dropboks.dao.FileContentDAO;
import dropboks.dao.FileMetadataDAO;
import dropboks.dao.MetadataDAO;
import dropboks.exceptions.AlreadyExistsException;
import dropboks.exceptions.NoRecordForundInDatabaseException;
import dropboks.model.DirectoryMetadata;
import dropboks.model.FileContent;
import dropboks.model.FileMetadata;
import org.jooq.exception.DataAccessException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static pl.edu.agh.kis.florist.db.tables.FileContents.FILE_CONTENTS;
import static pl.edu.agh.kis.florist.db.tables.FileMetadata.FILE_METADATA;
import static pl.edu.agh.kis.florist.db.tables.FolderMetadata.FOLDER_METADATA;

/**
 * Created by miwas on 19.01.17.
 */
public class DirectoryFileController {
    private DirectoryMetadataDAO dirMetaRepo;

    private FileMetadataDAO filesMetaRepo;
    private FileContentDAO filesContRepo;

    DirectoryFileController(){
        this.dirMetaRepo = new DirectoryMetadataDAO(
                DirectoryMetadata.class,
                FOLDER_METADATA);

        this.filesMetaRepo = new FileMetadataDAO(
                FileMetadata.class,
                FILE_METADATA
        );

        this.filesContRepo = new FileContentDAO(
                FileContent.class,
                FILE_CONTENTS
        );
    }

    public MetadataDAO resolveMetaType(String path) throws NoRecordForundInDatabaseException {
        if ( dirMetaRepo.existsBySecondId(path)){ // so it's directory
            return dirMetaRepo;
        } else if (filesMetaRepo.existsBySecondId(path)) {
            return filesMetaRepo;
        } else throw new NoRecordForundInDatabaseException("Not found in database.");
    }

    public DirectoryMetadata createDirectory(String path) throws AlreadyExistsException, NoRecordForundInDatabaseException {
        if ( dirMetaRepo.existsBySecondId(path)){
            throw new AlreadyExistsException("This directory already exists");
        }

        if (!dirMetaRepo.existsBySecondId(PathResolver.getParentPath(path))){
            throw new NoRecordForundInDatabaseException("A path in which you want to store new directory doesn't exist");
        }

        DirectoryMetadata result = dirMetaRepo.store(path);
        return result;
    }

    public DirectoryMetadata createDirectoryForUser(String userName) throws AlreadyExistsException{
        if ( dirMetaRepo.existsBySecondId(userName)){
            throw new AlreadyExistsException("This directory already exists");
        }

        DirectoryMetadata directoryMetadata = new DirectoryMetadata(
                userName,
                userName.toLowerCase(),
                userName,
                0,
                dirMetaRepo.time()
        );

        DirectoryMetadata result = dirMetaRepo.store(directoryMetadata);
        return result;
    }



    public Object rename(String oldPath, String newPath) throws NoRecordForundInDatabaseException {
        MetadataDAO repo;
        repo = resolveMetaType(oldPath);
        if ( repo.getClass() == FileMetadataDAO.class) {
            return repo.rename(oldPath, newPath);
        }

        // find directory by path
        DirectoryMetadata directoryMetadata = dirMetaRepo.findBySecondId(oldPath);

        // change this direcotry
        directoryMetadata.setName(PathResolver.getName(newPath));
        directoryMetadata.setPathLower(newPath.toLowerCase());
        directoryMetadata.setPathDisplay(newPath);

        // get lists of children below
        List<DirectoryMetadata> childrenList = dirMetaRepo.getListOfChildren(directoryMetadata.getFolderId());
        List<FileMetadata> filesList = filesMetaRepo.getListOfChildren(directoryMetadata.getFolderId());

        // rename children
        for (DirectoryMetadata child : childrenList){
            rename(oldPath+"/"+child.getName(), newPath+"/"+child.getName());
        }

        for (FileMetadata child : filesList){
            filesMetaRepo
                    .rename(
                            oldPath+"/"+child.getName(),
                            newPath+"/"+child.getName()
                    );
        }

        // rename itself
        return dirMetaRepo.rename(oldPath, newPath);
    }

    public void delete(String path) throws NoRecordForundInDatabaseException, InvalidParameterException{

        // disable removing main directory
        if ( PathResolver.isHomeDirectory(path)){
            throw new InvalidParameterException("Sorry, sir, I can't remove home directory.");
        }

        MetadataDAO repo = resolveMetaType(path);

        if ( repo.getClass() == FileMetadataDAO.class) {
            repo.delete(path);
            return;
        }

        DirectoryMetadata object = dirMetaRepo.findBySecondId(path);
        Integer id = object.getFolderId();

        List<DirectoryMetadata> childrenList = dirMetaRepo.getListOfChildren(id);
        List<FileMetadata> fileList = filesMetaRepo.getListOfChildren(id);

        // order your children to kill theirs children and themselves
        for ( DirectoryMetadata child : childrenList){
            delete(child.getPathDisplay());
        }

        // destroy your files
        for ( FileMetadata child : fileList){
            filesMetaRepo.delete(child);
            filesContRepo.deleteById(child.getFileId());
        }

        // kill yourself
        dirMetaRepo.deleteById(id);
    }

    public Object getMetaData(String path) throws NoRecordForundInDatabaseException{

        MetadataDAO repo;
        try {
            repo = resolveMetaType(path);
        } catch (NoRecordForundInDatabaseException e){
            throw e;
        }

        return repo.getMetaData(path);
    }

    public FileMetadata uploadFile(String pathToFile, TransferFile file) throws InvalidParameterException{
        if (!dirMetaRepo.existsBySecondId(PathResolver.getParentPath(pathToFile))) {
            throw new InvalidParameterException("The path to file doesn't exist");
        }

        while (filesMetaRepo.existsBySecondId(pathToFile)){
            pathToFile += (int) Math.random();
        }


        FileMetadata fileMetadata = filesMetaRepo.create(
                pathToFile,
                dirMetaRepo.findBySecondId(PathResolver.getParentPath(pathToFile)).getFolderId(),
                file.size()
        );
        FileMetadata result = filesMetaRepo.store(fileMetadata);

        FileContent fileContent = new FileContent(file.getBytes());
        filesContRepo.store(fileContent);

        return result;
    }

    public Object move(String oldPath, String newPath) throws InvalidParameterException{

        MetadataDAO repo = resolveMetaType(oldPath);

        if ( dirMetaRepo.existsBySecondId(newPath)){
            throw new InvalidParameterException("Directory already exists");
        }

        if ( !dirMetaRepo.existsBySecondId(PathResolver.getParentPath(newPath))){
            throw new InvalidParameterException("Path to directory doesn't exist");
        }

        repo.move(oldPath, newPath);
        Object result = rename(oldPath, newPath);
        return result;
    }

    public List getListFolderContent(String path, boolean recursive) throws InvalidParameterException {
        DirectoryMetadata parentDirectory;

        if ( !dirMetaRepo.existsBySecondId(path)){
            throw new InvalidParameterException("Doesn't exist");
        }

        try {
            parentDirectory = dirMetaRepo.findBySecondId(path);
        } catch (NullPointerException | DataAccessException ex) {
            throw new InvalidParameterException("Error");
        }

        Integer directoryId = parentDirectory.getFolderId();
        if ( !recursive ){
            return dirMetaRepo.getListOfChildren(directoryId);
        }

        ArrayList<Object> metadataList = new ArrayList();
        List<DirectoryMetadata> childrenList = dirMetaRepo.getListOfChildren(directoryId);

        metadataList.add(dirMetaRepo.findById(directoryId));  // disordered version
        for (DirectoryMetadata child : childrenList) {
            /*
            ordered version
            metadataList.add(
                    dirMetaRepo.getMetadataWithChildren(child.getFolderId(),
                            getListFolderContent(path + "/" + child.getName(), true))
            );
            */
            metadataList.add(getListFolderContent(path + "/" + child.getName(), true));  // disordered version
        }
        metadataList.add(filesMetaRepo.getListOfChildren(directoryId));

        return metadataList;
    }

    public FileMetadata download(String path) throws InvalidParameterException{
        if ( !filesMetaRepo.existsBySecondId(path)){
            throw new InvalidParameterException("Doesn't exist");
        }

        FileMetadata fileMetadata = filesMetaRepo.findBySecondId(path);
        Integer id = fileMetadata.getFileId();
        FileContent fileContent = filesContRepo.findById(id);
        TransferFile file = new TransferFile(fileContent.getContents());
        return fileMetadata.getMetadataWithFile(file);

    }
}
