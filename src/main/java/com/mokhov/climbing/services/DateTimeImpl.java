package com.mokhov.climbing.services;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DateTimeImpl implements DateTime  {
    @Override
    public Date getDate() {
        return new Date();
    }
}
