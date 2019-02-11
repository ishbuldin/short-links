package ru.ishbuldin.andrei.shortlinks;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface LinkRepository extends CrudRepository<Link, Integer> {

    List<Link> findByCount(int count);
    Link findByLink(String url);
    Link findByOriginal(String url);
    List<Link> findAll();
    Page<Link> findAll(Pageable pageable);
    boolean existsByLink(String url);
    boolean existsByOriginal(String url);

}