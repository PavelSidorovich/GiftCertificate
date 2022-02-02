package com.epam.esm.gcs.repository.extractor;

import com.epam.esm.gcs.model.GiftCertificateModel;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.List;

public interface MultipleCertificateTagSetExtractor
        extends ResultSetExtractor<List<GiftCertificateModel>> {
}
