package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * {@link CrudRepository Repository} of {@link BundleKeyTranslationEntity translation entities}.
 *
 * @author Sebastien Gerard
 */
@Repository
public interface BundleKeyTranslationRepository extends CrudRepository<BundleKeyTranslationEntity, String>, BundleKeyTranslationRepositoryCustom {

}
