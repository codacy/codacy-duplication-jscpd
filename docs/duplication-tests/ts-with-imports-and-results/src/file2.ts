// Common imports for both files
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { map, filter, switchMap } from 'rxjs/operators';

const b = {
  'a': [
    'a',
    'b',
    'c',
    'd',
    'e',
    'f',
    'g',
    'h',
    'i',
    'l',
    'm',
    'n',
    'o',
    'p',
    'q',
    'r',
    's',
    't',
    'v',
    'z',
  ],
  'b': [
    'a',
    'b',
    'c',
    'd',
    'e'
  ]
};

function aFunction(n: number): number {
  return n + 1
}
