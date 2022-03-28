package com.epam.esm.gcs.model;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {

    Long getId();

}
