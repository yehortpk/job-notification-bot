package com.github.yehortpk.notifier.entities;

import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@ToString(callSuper = true)
@Getter
public abstract class MultiplePageCompanySite extends CompanySiteImpl {}
