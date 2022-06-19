package com.yas.media.repository;

import com.yas.media.model.Media;
import com.yas.media.viewModel.NoFileMediaVm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    @Query(value = "select new com.yas.media.viewModel.NoFileMediaVm(m.id, m.caption, m.fileName, m.mediaType) from Media m where m.id = ?1")
    NoFileMediaVm findByIdWithoutFileInReturn(Long id);
}
