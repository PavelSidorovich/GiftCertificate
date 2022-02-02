package com.epam.esm.gcs.repository.extractor;

import com.epam.esm.gcs.model.GiftCertificateModel;
import org.springframework.jdbc.core.ResultSetExtractor;

public interface SingleCertificateTagSetExtractor
        extends ResultSetExtractor<GiftCertificateModel> {
}
