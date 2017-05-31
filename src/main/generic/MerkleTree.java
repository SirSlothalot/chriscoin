package main.generic;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MerkleTree {

	public static byte[] root(Object[] t) throws IOException {
		int len = t.length;
		byte[][] hashes = new byte[len][];

		for (int i = 0; i < len; i++) {
			try {
				hashes[i] = Hasher.doubleHash(t[i]);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		int newLen;
		newLen = 2;
		while (newLen < len) {
			newLen = newLen * 2;
		}

		byte[][] leafs = new byte[newLen][];

		for (int i = 0; i < len; i++) {
			leafs[i] = hashes[i];
		}
		// Filling with duplicates of last element
		for (int i = len; i < newLen; i++) {
			leafs[i] = hashes[len - 1];
		}
		return combineHashes(leafs);
	}

	private static byte[] combineHashes(byte[][] hashes) {
		if (hashes.length == 2) {
			byte[] left = hashes[0];
			byte[] right = hashes[1];
			byte[] combined = new byte[left.length + right.length];
			System.arraycopy(left, 0, combined, 0, left.length);
			System.arraycopy(right, 0, combined, left.length, right.length);
			byte[] hashed = new byte[32];
			try {
				hashed = Hasher.doubleHash(combined);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return hashed;
		} else {
			byte[][] left = new byte[hashes.length / 2][];
			byte[][] right = new byte[hashes.length / 2][];
			for (int i = 0; i < hashes.length / 2; i++) {
				left[i] = hashes[i];
			}
			for (int i = hashes.length / 2; i < hashes.length; i++) {
				right[i - (hashes.length / 2)] = hashes[i];
			}
			byte[] newLeft = combineHashes(left);
			byte[] newRight = combineHashes(right);
			byte[] combined = new byte[newLeft.length + newRight.length];
			System.arraycopy(newLeft, 0, combined, 0, newLeft.length);
			System.arraycopy(newRight, 0, combined, newLeft.length, newRight.length);
			try {
				return Hasher.doubleHash(combined);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static boolean compareMerkle(Object[] t, Object target) throws IOException {

		int targetIndex = 0;
		int len = t.length;
		byte[] targetDoubleHash = null;
		try {
			targetDoubleHash = Hasher.doubleHash(target);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		byte[][] hashes = new byte[len][];

		for (int i = 0; i < len; i++) {
			try {
				hashes[i] = Hasher.doubleHash(t[i]);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}

		int newLen;
		newLen = 2;
		while (newLen < len) {
			newLen = newLen * 2;
		}

		byte[][] leafs = new byte[newLen][];

		for (int i = 0; i < len; i++) {
			leafs[i] = hashes[i];
			if (Arrays.equals(leafs[i], targetDoubleHash)) {
				targetIndex = i;
			}
		}
		// Filling with duplicates of last element
		for (int i = len; i < newLen; i++) {
			leafs[i] = hashes[len - 1];
		}

		int pathLength = (int) (Math.log(leafs.length) / Math.log(2));
		System.out.print(pathLength + " -- ");
		byte[][] hashPath = new byte[pathLength][];
		Boolean[] leftSibling = new Boolean[pathLength];

		byte[] merkleRoot = combineHashesPath(leafs, targetIndex, pathLength, hashPath, leftSibling);

		return comparePath(hashPath, targetDoubleHash, merkleRoot, targetIndex, leftSibling);
	}

	private static byte[] combineHashesPath(byte[][] hashes, int targetIndex, int level, byte[][] hashPath,
			Boolean[] leftSibling) {
		if (hashes.length == 2) {
			byte[] left = hashes[0];
			byte[] right = hashes[1];
			if (targetIndex / level % 2 == 0) {
				hashPath[level - 1] = right;
				leftSibling[level - 1] = false;
			} else {
				hashPath[level - 1] = left;
				leftSibling[level - 1] = true;
			}
			byte[] combined = new byte[left.length + right.length];
			System.arraycopy(left, 0, combined, 0, left.length);
			System.arraycopy(right, 0, combined, left.length, right.length);
			byte[] hashed = new byte[32];
			try {
				hashed = Hasher.doubleHash(combined);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			return hashed;
		} else {
			byte[][] left = new byte[hashes.length / 2][];
			byte[][] right = new byte[hashes.length / 2][];
			for (int i = 0; i < hashes.length / 2; i++) {
				left[i] = hashes[i];
			}
			for (int i = hashes.length / 2; i < hashes.length; i++) {
				right[i - (hashes.length / 2)] = hashes[i];
			}
			byte[] newLeft = combineHashesPath(left, targetIndex, level - 1, hashPath, leftSibling);
			byte[] newRight = combineHashesPath(right, targetIndex, level - 1, hashPath, leftSibling);
			if (targetIndex / level % 2 == 0) {
				hashPath[level - 1] = newRight;
				leftSibling[level - 1] = false;
			} else {
				hashPath[level - 1] = newLeft;
				leftSibling[level - 1] = true;
			}
			byte[] combined = new byte[newLeft.length + newRight.length];
			System.arraycopy(newLeft, 0, combined, 0, newLeft.length);
			System.arraycopy(newRight, 0, combined, newLeft.length, newRight.length);
			try {
				return Hasher.doubleHash(combined);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	private static boolean comparePath(byte[][] hashPath, byte[] check, byte[] merkleRoot, int targetIndex,
			Boolean[] leftSibling) {

		byte[] hash = check;

		for (int i = 0; i < hashPath.length; i++) {
			byte[] combined = new byte[64];
			// System.out.println(N + " % (" + targetIndex + "/(" + i + " +
			// 1))");

			if (!leftSibling[i]) {
				System.arraycopy(hash, 0, combined, 0, 32);
				System.arraycopy(hashPath[i], 0, combined, 32, 32);
			} else {
				System.arraycopy(hashPath[i], 0, combined, 0, 32);
				System.arraycopy(hash, 0, combined, 32, 32);
			}
			try {
				hash = Hasher.doubleHash(combined);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		}

		return Arrays.equals(hash, merkleRoot);
	}

}
