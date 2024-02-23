// Common imports for both files that should be ignored
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { map, filter, switchMap } from 'rxjs/operators';

const b = {
  'a': [
    '123a',
    '123b',
    '123c',
    '123d',
    '123e',
    '123f',
    '123g',
    '123h',
    '123i',
    '123l',
    '123m',
    '123n',
    '123o',
    '123p',
    '123q',
    '123r',
    '123s',
    '123t',
    '123v',
    '123z',
  ],
  'b': [
    '123a',
    '123b',
    '123c',
    '123d',
    '123e'
  ]
};

function aPossibleFunction(n: number): number {
  return n + 1
}
