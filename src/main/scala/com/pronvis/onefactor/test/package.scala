package com.pronvis.onefactor

import com.pronvis.onefactor.test.api.Responses.ErrorResponse

package object test {

  type Response[A] = Either[ErrorResponse, A]

  def OK[A](resp: A): Response[A] = Right(resp)

  def FAIL(resp: ErrorResponse) = Left(resp)
}
